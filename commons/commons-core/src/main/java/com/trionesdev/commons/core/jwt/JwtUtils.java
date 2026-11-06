package com.trionesdev.commons.core.jwt;

import com.google.common.collect.Maps;
import com.nimbusds.jwt.JWTClaimsSet;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.trionesdev.commons.core.jwt.ClaimsKeyConstant.*;

public final class JwtUtils {

    public static Map<String, Object> genClaims(Object userId, JwtClaims claim) {
        Map<String, Object> claims = Maps.newHashMap();
        claims.put(ACTOR_ID, userId);
        claims.put(ACTOR_USER_ID, userId);
        claims.put(ACTOR_ROLE, claim.getRole());
        claims.put(ACTOR_TENANT_ID, claim.getTenantId());
        claims.put(ACTOR_MEMBER_ID, claim.getMemberId());
        claims.put(ACTOR_ATTRIBUTES, claim.getAttributes());
        return claims;
    }

    public static Map<String, Object> genClaims(JwtClaims claim) {
        return genClaims(claim.getUserId(), claim);
    }

    public static JWTClaimsSet genJWTClaimsSet(String subject, JwtClaims claim, Duration expiration) {
        Map<String, Object> claims = genClaims(claim);
        return genJWTClaimsSet(subject, claims, expiration);
    }

    public static JWTClaimsSet genJWTClaimsSet(String subject, Map<String, Object> claims, Duration expiration) {
        Objects.requireNonNull(claims, "claims");
        Instant now = Instant.now();
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.subject(subject);
        claims.forEach(builder::claim);
        return builder.issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(expiration))).build();
    }

}
