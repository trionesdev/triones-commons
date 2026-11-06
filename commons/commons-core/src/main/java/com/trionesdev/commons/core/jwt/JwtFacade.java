package com.trionesdev.commons.core.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Reuse this instance. Generated keys are ephemeral; supply a JWK for persistent keys.
 */
public class JwtFacade {
    private final JWSAlgorithm algorithm;
    private final JWK signingKey;
    private final Duration expiration;

    public JwtFacade(JwtConfig config) {
        this(config, null);
    }

    /**
     * Supply a private RSA/EC key or a symmetric octet key for shared deployments.
     */
    public JwtFacade(JwtConfig config, JWK signingKey) {
        Objects.requireNonNull(config, "config");
        this.algorithm = Objects.requireNonNull(config.getAlgorithm(), "algorithm");
        if (config.getFamily() == null || !config.getFamily().contains(algorithm)) {
            throw new IllegalArgumentException("Algorithm " + algorithm + " must belong to family " + config.getFamily());
        }
        this.expiration = Duration.ofSeconds(config.getExpiration());
        try {
            this.signingKey = signingKey == null ? createKey(config) : signingKey;
            // Validate key type, curve and size before accepting any requests.
            new DefaultJWSSignerFactory().createJWSSigner(this.signingKey, algorithm);
        } catch (JOSEException e) {
            throw new IllegalArgumentException("Invalid JWT signing configuration for " + algorithm, e);
        }
    }

    private JWK createKey(JwtConfig config) throws JOSEException {
        String keyId = UUID.randomUUID().toString();
        if (JWSAlgorithm.Family.RSA.contains(algorithm)) {
            return new RSAKeyGenerator(2048).keyID(keyId).generate();
        }
        if (JWSAlgorithm.Family.EC.contains(algorithm)) {
            Curve curve;
            if (JWSAlgorithm.ES256.equals(algorithm)) curve = Curve.P_256;
            else if (JWSAlgorithm.ES384.equals(algorithm)) curve = Curve.P_384;
            else if (JWSAlgorithm.ES512.equals(algorithm)) curve = Curve.P_521;
            else throw new IllegalArgumentException("Unsupported EC algorithm: " + algorithm);
            return new ECKeyGenerator(curve).keyID(keyId).generate();
        }
        if (JWSAlgorithm.Family.HMAC_SHA.contains(algorithm)) {
            int bits = JWSAlgorithm.HS256.equals(algorithm) ? 256
                    : JWSAlgorithm.HS384.equals(algorithm) ? 384 : 512;
            if (config.getSecret() != null) {
                byte[] secret = config.getSecret().getBytes(StandardCharsets.UTF_8);
                if (secret.length > bits / 8) {
                    throw new IllegalArgumentException(algorithm + " secret must not exceed " + bits / 8 + " bytes");
                }
                secret = java.util.Arrays.copyOf(secret, bits / 8);
                return new OctetSequenceKey.Builder(secret).keyID(keyId).build();
            }
            return new OctetSequenceKeyGenerator(bits).keyID(keyId).generate();
        }
        throw new IllegalArgumentException("Unsupported JWT algorithm: " + algorithm);
    }

    public String generate(String subject, Map<String, Object> claims) {
        if (expiration.isNegative() || expiration.isZero()) {
            throw new IllegalArgumentException("Expiration must be positive");
        }
        return generate(JwtUtils.genJWTClaimsSet(subject, claims, expiration));
    }

    public String generate(JWTClaimsSet claimsSet) {
        Objects.requireNonNull(claimsSet, "claimsSet");
        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(algorithm)
                    .type(JOSEObjectType.JWT).keyID(signingKey.getKeyID()).build(), claimsSet);
            jwt.sign(new DefaultJWSSignerFactory().createJWSSigner(signingKey, algorithm));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
    }

    public JWTClaimsSet parse(String token) {
        Objects.requireNonNull(token, "token");
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!algorithm.equals(jwt.getHeader().getAlgorithm())) {
                throw new JOSEException("Expected JWT algorithm: " + algorithm);
            }
            java.security.Key key;
            if (signingKey instanceof RSAKey) key = ((RSAKey) signingKey).toRSAPublicKey();
            else if (signingKey instanceof ECKey) key = ((ECKey) signingKey).toECPublicKey();
            else if (signingKey instanceof OctetSequenceKey) key = ((OctetSequenceKey) signingKey).toSecretKey();
            else throw new JOSEException("Unsupported verification key");
            if (!jwt.verify(new DefaultJWSVerifierFactory().createJWSVerifier(jwt.getHeader(), key))) {
                throw new JOSEException("Invalid JWT signature");
            }
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Date now = new Date();
            if (claims.getExpirationTime() == null || !claims.getExpirationTime().after(now)) {
                throw new JOSEException("JWT is expired or missing expiration time");
            }
            if (claims.getNotBeforeTime() != null && claims.getNotBeforeTime().after(now)) {
                throw new JOSEException("JWT is not yet valid");
            }
            return claims;
        } catch (ParseException | JOSEException e) {
            throw new IllegalArgumentException("Failed to parse JWT", e);
        }
    }

    public Map<String, Object> parseClaims(String token) {
        return parse(token).getClaims();
    }


    public Map<String, Object> getPublicJwkSet() {
        if (signingKey instanceof OctetSequenceKey) {
            throw new UnsupportedOperationException("HMAC keys have no public JWK");
        }
        return signingKey.toPublicJWK().toJSONObject();
    }
}
