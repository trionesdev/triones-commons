package com.trionesdev.commons.core.jwt;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

import static com.trionesdev.commons.core.jwt.ClaimsKeyConstant.*;

public class JwtFacade {

    private final JwtConfig jwtConfig;

    public JwtFacade(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generate(Object userId, JwtClaims claim){
        if (Objects.isNull(userId)) {
            return null;
        }
        Map<String, Object> claims = Maps.newHashMap();
        claims.put(ACTOR_ID, userId);
        claims.put(ACTOR_USER_ID, userId);
        claims.put(ACTOR_ROLE, claim.getRole());
        claims.put(ACTOR_TENANT_ID, claim.getTenantId());
        claims.put(ACTOR_TENANT_MEMBER_ID, claim.getTenantMemberId());
        claims.put(ACTOR_ATTRIBUTES, claim.getAttributes());
        return JwtUtils.generateToken(jwtConfig, String.valueOf(userId), claims);
    }

//    public String generate(Object userId, String role, Object tenantId, Object tenantMemberId, Map<String, String> attributes) {
//        if (Objects.isNull(userId)) {
//            return null;
//        }
//        Map<String, Object> claims = Maps.newHashMap();
//        claims.put(ACTOR_USER_ID, userId);
//        claims.put(ACTOR_ROLE, role);
//        claims.put(ACTOR_TENANT_ID, tenantId);
//        claims.put(ACTOR_TENANT_MEMBER_ID, tenantMemberId);
//        claims.put(ACTOR_ATTRIBUTES, attributes);
//        return JwtUtils.generateToken(jwtConfig, String.valueOf(userId), claims);
//    }

//    public String generate(Object userId, String role, Object tenantId, Object tenantMemberId) {
//        return generate(userId, role, tenantId, tenantMemberId, null);
//    }

    public String generateUser(Object userId) {
        return generate(userId, JwtClaims.builder().role("USER").build());
    }

//    public String generateUser(Object userId, Map<String, Object> attributes) {
//        return generate(userId, JwtClaim.builder().role("USER").attributes(attributes).build());
//    }

    public String generate(Object userId, String role) {
        return generate(userId, JwtClaims.builder().role(role).build());
    }

//    public String generate(Object userId, String role, Object tenantId) {
//        return generate(userId, role, tenantId, null, null);
//    }

    public String generate(String subject, Map<String, Object> claims) {
        return JwtUtils.generateToken(jwtConfig, subject, claims);
    }

    public Map<String, Object> parse(String token) {
        return JwtUtils.parseClaims(jwtConfig, token);
    }

}
