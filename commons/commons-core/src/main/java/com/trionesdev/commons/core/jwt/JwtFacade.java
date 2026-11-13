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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * JWT 签发与校验门面，线程安全，请复用实例。
 * <p>
 * 未提供密钥时自动生成临时密钥（重启即失效）；多实例/持久化部署请通过构造器传入 JWK。
 * <p>
 * <b>密钥轮换</b>：始终只用 {@code signingKey} 签发；{@code verificationKeys} 中的旧密钥仅用于校验，
 * 按 JWT 头的 {@code kid} 选择验证密钥。轮换时把旧密钥放入 verificationKeys、新密钥作为 signingKey，
 * 等旧 token 全部过期（≥ expiration）后再移除旧密钥。
 */
public class JwtFacade {
    private final JWSAlgorithm algorithm;
    private final Duration expiration;
    private final JWK signingKey;
    /** 签名公钥 + 验证公钥（不含私钥字段）；HMAC 为空。 */
    private final List<JWK> publicKeys;
    private final JWSHeader header;
    // nimbus 的 signer/verifier 均为线程安全，构造时创建一次即可。
    private final JWSSigner signer;
    /** kid → verifier，签名密钥在前。 */
    private final Map<String, JWSVerifier> verifiersByKid;
    /** 所有 verifier（签名密钥在前），用于 JWT 头没有 kid 的情况。 */
    private final List<JWSVerifier> verifiers;

    public JwtFacade(JwtConfig config) {
        this(config, null);
    }

    /**
     * @param signingKey RSA/EC 私钥，或 HMAC 对称密钥；为 null 时按算法自动生成
     */
    public JwtFacade(JwtConfig config, JWK signingKey) {
        this(config, signingKey, List.of());
    }

    /**
     * @param signingKey       RSA/EC 私钥，或 HMAC 对称密钥；为 null 时按算法自动生成
     * @param verificationKeys 仅用于校验的额外密钥（如轮换前的旧密钥）。RSA/EC 可只提供公钥；
     *                         未设置 kid 时使用 JWK thumbprint
     */
    public JwtFacade(JwtConfig config, JWK signingKey, Collection<? extends JWK> verificationKeys) {
        Objects.requireNonNull(config, "config");
        this.algorithm = Objects.requireNonNull(config.getAlgorithm(), "algorithm");
        if (!isRsa() && !isEc() && !isHmac()) {
            throw new IllegalArgumentException("Unsupported JWT algorithm: " + algorithm);
        }
        this.expiration = Duration.ofSeconds(config.getExpiration());
        Map<String, JWSVerifier> byKid = new LinkedHashMap<>();
        List<JWSVerifier> all = new ArrayList<>();
        List<JWK> publics = new ArrayList<>();
        try {
            this.signingKey = signingKey == null ? createKey(config.getSecret()) : signingKey;
            // 签名器工厂会校验密钥类型、曲线和长度，配置错误在启动阶段即暴露。
            this.signer = new DefaultJWSSignerFactory().createJWSSigner(this.signingKey, algorithm);
            addVerifier(this.signingKey, byKid, all, publics);
            if (verificationKeys != null) {
                for (JWK key : verificationKeys) {
                    Objects.requireNonNull(key, "verificationKey");
                    if (sameKey(key, this.signingKey)) {
                        continue; // 当前签名密钥重复出现在验证列表中，忽略
                    }
                    addVerifier(withThumbprintKid(key), byKid, all, publics);
                }
            }
        } catch (JOSEException e) {
            throw new IllegalArgumentException("Invalid JWT signing configuration for " + algorithm, e);
        }
        this.verifiersByKid = Collections.unmodifiableMap(byKid);
        this.verifiers = List.copyOf(all);
        this.publicKeys = List.copyOf(publics);
        this.header = new JWSHeader.Builder(algorithm)
                .type(JOSEObjectType.JWT)
                .keyID(this.signingKey.getKeyID())
                .build();
    }

    private void addVerifier(JWK key, Map<String, JWSVerifier> byKid, List<JWSVerifier> all, List<JWK> publics)
            throws JOSEException {
        requireCompatible(key);
        JWSVerifier verifier = createVerifier(key);
        String kid = key.getKeyID();
        if (kid != null && byKid.putIfAbsent(kid, verifier) != null) {
            throw new IllegalArgumentException("Duplicate JWT key id: " + kid);
        }
        all.add(verifier);
        if (!(key instanceof OctetSequenceKey)) {
            publics.add(key.toPublicJWK());
        }
    }

    /** 验证密钥类型/曲线必须与算法匹配，否则启动即报错，而不是在校验时静默失败。 */
    private void requireCompatible(JWK key) {
        boolean ok = isRsa() ? key instanceof RSAKey
                : isEc() ? key instanceof ECKey && ecCurve().equals(((ECKey) key).getCurve())
                : key instanceof OctetSequenceKey;
        if (!ok) {
            throw new IllegalArgumentException("JWT key " + key.getKeyID() + " (" + key.getKeyType()
                    + ") is not compatible with " + algorithm);
        }
    }

    private static JWK withThumbprintKid(JWK key) throws JOSEException {
        // HMAC 的 thumbprint 是对称密钥的哈希，弱 secret 可被离线爆破，不作为 kid 暴露
        if (key.getKeyID() != null || key instanceof OctetSequenceKey) {
            return key;
        }
        String kid = key.computeThumbprint().toString();
        if (key instanceof RSAKey) return new RSAKey.Builder((RSAKey) key).keyID(kid).build();
        if (key instanceof ECKey) return new ECKey.Builder((ECKey) key).keyID(kid).build();
        if (key instanceof OctetSequenceKey) return new OctetSequenceKey.Builder((OctetSequenceKey) key).keyID(kid).build();
        throw new JOSEException("Unsupported verification key: " + key.getKeyType());
    }

    private static boolean sameKey(JWK a, JWK b) throws JOSEException {
        return a.getClass().equals(b.getClass())
                && Objects.equals(a.getKeyID(), b.getKeyID())
                && a.computeThumbprint().equals(b.computeThumbprint());
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
            if (!verify(jwt)) {
                throw new JOSEException("Invalid JWT signature");
            }
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            validateTimes(claims);
            return claims;
        } catch (ParseException | JOSEException e) {
            throw new IllegalArgumentException("Failed to parse JWT", e);
        }
    }

    /**
     * kid 命中时只用对应密钥校验；kid 缺失或未命中时依次尝试所有受信密钥。
     * <p>
     * kid 只是选择密钥的提示，不参与信任判断：签名必须能被某个已配置的密钥验证通过。
     * 未命中时回退尝试，是为了兼容 kid 不稳定的密钥（例如由 secret 生成的 HMAC 密钥，各实例 kid 不同）。
     */
    private boolean verify(SignedJWT jwt) throws JOSEException {
        String kid = jwt.getHeader().getKeyID();
        JWSVerifier matched = kid == null ? null : verifiersByKid.get(kid);
        if (matched != null) {
            return jwt.verify(matched);
        }
        for (JWSVerifier verifier : verifiers) {
            if (jwt.verify(verifier)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> parseClaims(String token) {
        return parse(token).getClaims();
    }

    /**
     * 返回标准 JWK Set（RFC 7517 §5）JSON：{@code {"keys":[{...}]}}，不含私钥字段，
     * 可直接作为 {@code /.well-known/jwks.json} 响应体。
     * 包含签名公钥（第一个）和所有验证公钥，便于远程校验方在轮换期间同时认可新旧 token。
     *
     * @throws UnsupportedOperationException HMAC 密钥没有可公开的部分
     */
    public Map<String, Object> getPublicJwkSet() {
        requirePublicKey();
        return new JWKSet(publicKeys).toJSONObject();
    }

    /**
     * @return 当前签名密钥的 kid（JWT 头中的 kid），可能为 null
     */
    public String getSigningKeyId() {
        return signingKey.getKeyID();
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
        if (signingKey instanceof OctetSequenceKey) {
            throw new UnsupportedOperationException("HMAC keys have no public JWK");
        }
        return publicKeys.get(0);
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
