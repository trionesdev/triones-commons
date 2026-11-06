package com.trionesdev.commons.core.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtConfig {
    private JWSAlgorithm.Family family; // 签名算法族
    private JWSAlgorithm algorithm; // 签名算法
    /**
     * 秘钥
     */
    private String secret;
    /**
     * 过期时间(单位 秒)
     */
    private int expiration;
}
