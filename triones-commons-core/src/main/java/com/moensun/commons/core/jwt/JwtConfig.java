package com.moensun.commons.core.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtConfig {
    /**
     * 秘钥
     */
    private String secret;
    /**
     * 过期时间(单位 秒)
     */
    private int expiration;
}
