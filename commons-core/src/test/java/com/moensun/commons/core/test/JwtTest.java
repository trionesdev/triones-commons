package com.moensun.commons.core.test;

import com.moensun.commons.core.jwt.JwtConfig;
import com.moensun.commons.core.jwt.JwtFacade;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtTest {

    @Test
    public void generateJwt() {
        JwtConfig jwtConfig = JwtConfig.builder().secret("123456789").expiration(3600).build();
        JwtFacade jwtFacade = new JwtFacade(jwtConfig);
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", "bane");
        String token = jwtFacade.generate("bane", claims);
        System.out.println(token);
    }

    @Test
    public void generateToken() {
        JwtConfig jwtConfig = JwtConfig.builder().secret("123456789").expiration(3600).build();
        JwtFacade jwtFacade = new JwtFacade(jwtConfig);
        String token = jwtFacade.generate("userId", "role", "tenantId", "tenantMemberId");
        System.out.println(token);
    }

}
