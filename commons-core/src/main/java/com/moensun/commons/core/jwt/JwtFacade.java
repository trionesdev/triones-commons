package com.moensun.commons.core.jwt;


import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;

import static com.moensun.commons.core.jwt.ClaimsKeyConstant.*;


public class JwtFacade {

    private final JwtConfig jwtConfig;

    public JwtFacade(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateUser(Object userId) {
        return generate(userId, null, null);
    }

    public String generate(Object userId, String role, Object tenantId) {
        if (Objects.isNull(userId)) {
            return null;
        }
        Map<String, Object> claims = Maps.newHashMap();
        claims.put(ACTOR_ID, userId);
        claims.put(ACTOR_ROLE, role);
        claims.put(ACTOR_TENANT_ID, tenantId);
        return JwtUtils.generateToken(jwtConfig, String.valueOf(userId), claims);
    }

    public String generate(String subject, Map<String, Object> claims) {
        return JwtUtils.generateToken(jwtConfig, subject, claims);
    }

    public Map<String, Object> parse(String token) {
        return JwtUtils.parseClaims(jwtConfig, token);
    }

}
