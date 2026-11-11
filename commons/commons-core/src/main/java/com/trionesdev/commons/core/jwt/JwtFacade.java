package com.trionesdev.commons.core.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * JWT 签发与校验门面，线程安全，请复用实例。
 * <p>
 * 未提供密钥时自动生成临时密钥（重启即失效）；多实例/持久化部署请通过构造器传入 JWK。
 */
public class JwtFacade {
    private final JWSAlgorithm algorithm;
    private final Duration expiration;
    private final JWK signingKey;
    /** 非对称算法对应的公钥；HMAC 为 null。 */
    private final JWK publicKey;
    private final JWSHeader header;
    // nimbus 的 signer/verifier 均为线程安全，构造时创建一次即可。
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    public JwtFacade(JwtConfig config) {
        this(config, null);
    }

    /**
     * @param signingKey RSA/EC 私钥，或 HMAC 对称密钥；为 null 时按算法自动生成
     */
    public JwtFacade(JwtConfig config, JWK signingKey) {
        Objects.requireNonNull(config, "config");
        this.algorithm = Objects.requireNonNull(config.getAlgorithm(), "algorithm");
        if (!isRsa() && !isEc() && !isHmac()) {
            throw new IllegalArgumentException("Unsupported JWT algorithm: " + algorithm);
        }
        this.expiration = Duration.ofSeconds(config.getExpiration());
        try {
            this.signingKey = signingKey == null ? createKey(config.getSecret()) : signingKey;
            // 签名器工厂会校验密钥类型、曲线和长度，配置错误在启动阶段即暴露。
            this.signer = new DefaultJWSSignerFactory().createJWSSigner(this.signingKey, algorithm);
            this.verifier = createVerifier(this.signingKey);
        } catch (JOSEException e) {
            throw new IllegalArgumentException("Invalid JWT signing configuration for " + algorithm, e);
        }
        this.publicKey = this.signingKey instanceof OctetSequenceKey ? null : this.signingKey.toPublicJWK();
        this.header = new JWSHeader.Builder(algorithm)
                .type(JOSEObjectType.JWT)
                .keyID(this.signingKey.getKeyID())
                .build();
    }

    // ---------------------------------------------------------------- generate

    public String generate(String subject, JwtClaims claim) {
        return generate(JwtUtils.genJWTClaimsSet(subject, claim, requirePositiveExpiration()));
    }

    public String generate(String subject, Map<String, Object> claims) {
        return generate(JwtUtils.genJWTClaimsSet(subject, claims, requirePositiveExpiration()));
    }

    public String generate(JWTClaimsSet claimsSet) {
        Objects.requireNonNull(claimsSet, "claimsSet");
        try {
            SignedJWT jwt = new SignedJWT(header, claimsSet);
            jwt.sign(signer);
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
    }

    // ------------------------------------------------------------------- parse

    /**
     * 校验算法、签名、exp、nbf 后返回声明。
     *
     * @throws IllegalArgumentException token 非法、签名无效或已过期
     */
    public JWTClaimsSet parse(String token) {
        Objects.requireNonNull(token, "token");
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            // 先比对算法，防止 alg 混淆攻击。
            if (!algorithm.equals(jwt.getHeader().getAlgorithm())) {
                throw new JOSEException("Unexpected JWT algorithm: " + jwt.getHeader().getAlgorithm());
            }
            if (!jwt.verify(verifier)) {
                throw new JOSEException("Invalid JWT signature");
            }
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            validateTimes(claims);
            return claims;
        } catch (ParseException | JOSEException e) {
            throw new IllegalArgumentException("Failed to parse JWT", e);
        }
    }

    public Map<String, Object> parseClaims(String token) {
        return parse(token).getClaims();
    }

    /**
     * 返回标准 JWK Set（RFC 7517 §5）JSON：{@code {"keys":[{...}]}}，不含私钥字段，
     * 可直接作为 {@code /.well-known/jwks.json} 响应体。
     *
     * @throws UnsupportedOperationException HMAC 密钥没有可公开的部分
     */
    public Map<String, Object> getPublicJwkSet() {
        return new JWKSet(requirePublicKey()).toJSONObject();
    }

    /**
     * 返回单个签名公钥的 JWK JSON，不含私钥字段。
     *
     * @throws UnsupportedOperationException HMAC 密钥没有可公开的部分
     */
    public Map<String, Object> getPublicJwk() {
        return requirePublicKey().toJSONObject();
    }

    private JWK requirePublicKey() {
        if (publicKey == null) {
            throw new UnsupportedOperationException("HMAC keys have no public JWK");
        }
        return publicKey;
    }

    // ----------------------------------------------------------------- helpers

    private Duration requirePositiveExpiration() {
        if (expiration.isNegative() || expiration.isZero()) {
            throw new IllegalArgumentException("Expiration must be positive");
        }
        return expiration;
    }

    private static void validateTimes(JWTClaimsSet claims) throws JOSEException {
        Date now = new Date();
        Date exp = claims.getExpirationTime();
        if (exp == null || !exp.after(now)) {
            throw new JOSEException("JWT is expired or missing expiration time");
        }
        Date nbf = claims.getNotBeforeTime();
        if (nbf != null && nbf.after(now)) {
            throw new JOSEException("JWT is not yet valid");
        }
    }

    private JWK createKey(String secret) throws JOSEException {
        String keyId = UUID.randomUUID().toString();
        if (isRsa()) {
            return new RSAKeyGenerator(2048).keyID(keyId).generate();
        }
        if (isEc()) {
            return new ECKeyGenerator(ecCurve()).keyID(keyId).generate();
        }
        int bytes = hmacKeyBytes();
        if (secret == null) {
            return new OctetSequenceKeyGenerator(bytes * 8).keyID(keyId).generate();
        }
        byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
        if (raw.length > bytes) {
            throw new IllegalArgumentException(algorithm + " secret must not exceed " + bytes + " bytes");
        }
        // 不足部分以 0 字节补齐到算法要求的最小长度。
        return new OctetSequenceKey.Builder(Arrays.copyOf(raw, bytes)).keyID(keyId).build();
    }

    private static JWSVerifier createVerifier(JWK key) throws JOSEException {
        if (key instanceof RSAKey) return new RSASSAVerifier(((RSAKey) key).toRSAPublicKey());
        if (key instanceof ECKey) return new ECDSAVerifier(((ECKey) key).toECPublicKey());
        if (key instanceof OctetSequenceKey) return new MACVerifier((OctetSequenceKey) key);
        throw new JOSEException("Unsupported verification key: " + key.getKeyType());
    }

    private Curve ecCurve() {
        if (JWSAlgorithm.ES256.equals(algorithm)) return Curve.P_256;
        if (JWSAlgorithm.ES384.equals(algorithm)) return Curve.P_384;
        if (JWSAlgorithm.ES512.equals(algorithm)) return Curve.P_521;
        throw new IllegalArgumentException("Unsupported EC algorithm: " + algorithm);
    }

    private int hmacKeyBytes() {
        if (JWSAlgorithm.HS256.equals(algorithm)) return 32;
        if (JWSAlgorithm.HS384.equals(algorithm)) return 48;
        return 64;
    }

    private boolean isRsa() {
        return JWSAlgorithm.Family.RSA.contains(algorithm);
    }

    private boolean isEc() {
        return JWSAlgorithm.Family.EC.contains(algorithm);
    }

    private boolean isHmac() {
        return JWSAlgorithm.Family.HMAC_SHA.contains(algorithm);
    }
}
