package com.trionesdev.commons.core.jwt;

import com.google.common.collect.Maps;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.trionesdev.commons.core.jwt.ClaimsKeyConstant.*;

/**
 * Creates and verifies RS256 JWTs and exposes the public signing key.
 * Reuse the instance so tokens share the same signing key.
 */
public class JwtTokenFacade {
    private final RSAKey signingKey;
    private final Duration expiration;


    /**
     * Creates an in-memory key pair. Supply a persistent key through the other
     * constructor when keys must survive restarts or be shared by multiple nodes.
     */
    public JwtTokenFacade(Duration expiration) throws JOSEException {
        this(new RSAKeyGenerator(2048).keyID(UUID.randomUUID().toString()).generate(), expiration);
    }

    public JwtTokenFacade(RSAKey signingKey, Duration expiration) {
        Objects.requireNonNull(signingKey, "signingKey");
        Objects.requireNonNull(expiration, "expiration");
        if (!signingKey.isPrivate() || signingKey.size() < 2048) {
            throw new IllegalArgumentException("Signing key must contain an RSA private key of at least 2048 bits");
        }
        if (expiration.isNegative() || expiration.isZero()) {
            throw new IllegalArgumentException("Expiration must be positive");
        }
        this.signingKey = signingKey.getKeyID() == null
                ? new RSAKey.Builder(signingKey).keyID(UUID.randomUUID().toString()).build() : signingKey;
        this.expiration = expiration;
    }

    public String generate(Object userId, JwtClaims claim) {
        if (Objects.isNull(userId)) {
            return null;
        }
        Map<String, Object> claims = Maps.newHashMap();
        claims.put(ACTOR_ID, userId);
        claims.put(ACTOR_USER_ID, userId);
        claims.put(ACTOR_ROLE, claim.getRole());
        claims.put(ACTOR_TENANT_ID, claim.getTenantId());
        claims.put(ACTOR_MEMBER_ID, claim.getMemberId());
        claims.put(ACTOR_TENANT_MEMBER_ID, claim.getTenantMemberId());
        claims.put(ACTOR_ATTRIBUTES, claim.getAttributes());
        return generate(String.valueOf(userId), claims);
    }

    public String generate(Map<String, Object> claims) {
        return generate(null, claims);
    }

    public String generate(String subject, Map<String, Object> claims) {
        Objects.requireNonNull(claims, "claims");
        Instant now = Instant.now();
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.subject(subject);
        claims.forEach(builder::claim);
        JWTClaimsSet claimsSet = builder.issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(expiration))).build();
        return generate(claimsSet);
    }

    public String generate(JWTClaimsSet claimsSet) {
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT).keyID(signingKey.getKeyID()).build(), claimsSet);
        try {
            jwt.sign(new RSASSASigner(signingKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwt.serialize();
    }


    /**
     * Verifies an RS256 token using this instance's public key and returns its claims.
     * Expiration is required; expired tokens and tokens not yet valid are rejected.
     *
     * @throws ParseException if the token or its claims are malformed
     * @throws JOSEException  if signature or time validation fails
     */
    @SneakyThrows
    public JWTClaimsSet parse(String token) {
        Objects.requireNonNull(token, "token");
        SignedJWT jwt = SignedJWT.parse(token);
        if (!JWSAlgorithm.RS256.equals(jwt.getHeader().getAlgorithm())) {
            throw new JOSEException("Only RS256 tokens are supported");
        }
        if (!jwt.verify(new RSASSAVerifier(getPublicKey()))) {
            throw new JOSEException("Invalid JWT signature");
        }
        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        Date now = new Date();
        Date expiresAt = claims.getExpirationTime();
        if (expiresAt == null || !expiresAt.after(now)) {
            throw new JOSEException("JWT is expired or missing expiration time");
        }
        Date notBefore = claims.getNotBeforeTime();
        if (notBefore != null && notBefore.after(now)) {
            throw new JOSEException("JWT is not yet valid");
        }
        return claims;
    }

    /**
     * Returns verified claims as a map, with the same validation as {@link #parse(String)}.
     */
    public Map<String, Object> parseClaims(String token) throws ParseException, JOSEException {
        return parse(token).getClaims();
    }

    public RSAPublicKey getPublicKey() throws JOSEException {
        return signingKey.toRSAPublicKey();
    }

    /**
     * Public JWKS response body for the /jwt/issuer endpoint. Contains no private key.
     */
    public Map<String, Object> getPublicJwkSet() {
        return new JWKSet(signingKey.toPublicJWK()).toJSONObject();
    }
}
