package com.trionesdev.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AuthToken {
    private String accessToken;
    /**
     * 用于换取新的 access_token（POST /public-api/auth/refresh-token）
     */
    private String refreshToken;
    /**
     * access_token 剩余有效期（秒）
     */
    private Instant expiresAt;
    /**
     * refresh_token 剩余有效期（秒）
     */
    private Instant refreshExpiresAt;
}
