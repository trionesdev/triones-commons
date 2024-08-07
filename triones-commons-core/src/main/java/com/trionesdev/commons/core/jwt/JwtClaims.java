package com.trionesdev.commons.core.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class JwtClaims {
    private String role;
    private Object tenantId;
    private Object tenantMemberId;
    private Map<String,Object> attributes;
}
