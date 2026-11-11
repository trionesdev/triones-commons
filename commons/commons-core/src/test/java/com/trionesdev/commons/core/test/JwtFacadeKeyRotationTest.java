package com.trionesdev.commons.core.test;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.SignedJWT;
import com.trionesdev.commons.core.jwt.JwtConfig;
import com.trionesdev.commons.core.jwt.JwtFacade;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtFacadeKeyRotationTest {

    private static JwtConfig config(JWSAlgorithm algorithm) {
        return JwtConfig.builder().algorithm(algorithm).expiration(3600).build();
    }

    @Test
    void oldTokensRemainValidDuringRotation() throws Exception {
        RSAKey oldKey = new RSAKeyGenerator(2048).keyID("2025").generate();
        RSAKey newKey = new RSAKeyGenerator(2048).keyID("2026").generate();
        String oldToken = new JwtFacade(config(JWSAlgorithm.RS256), oldKey).generate("old", Map.of());

        // 轮换：新密钥签发，旧密钥只需公钥即可校验
        JwtFacade rotated = new JwtFacade(config(JWSAlgorithm.RS256), newKey, List.of(oldKey.toPublicJWK()));
        assertEquals("old", rotated.parse(oldToken).getSubject());
        String newToken = rotated.generate("new", Map.of());
        assertEquals("2026", SignedJWT.parse(newToken).getHeader().getKeyID());
        assertEquals("2026", rotated.getSigningKeyId());
        assertEquals("new", rotated.parse(newToken).getSubject());

        // 尚未升级的实例（只有旧密钥 + 新密钥作为验证密钥）也能校验新 token
        JwtFacade preRotation = new JwtFacade(config(JWSAlgorithm.RS256), oldKey, List.of(newKey.toPublicJWK()));
        assertEquals("new", preRotation.parse(newToken).getSubject());

        // 移除旧密钥后旧 token 失效
        JwtFacade afterRotation = new JwtFacade(config(JWSAlgorithm.RS256), newKey);
        assertThrows(IllegalArgumentException.class, () -> afterRotation.parse(oldToken));
    }

    @Test
    void untrustedKeyRejectedEvenWithUnknownKid() throws Exception {
        RSAKey signing = new RSAKeyGenerator(2048).keyID("a").generate();
        RSAKey other = new RSAKeyGenerator(2048).keyID("b").generate();
        String token = new JwtFacade(config(JWSAlgorithm.RS256), other).generate("u", Map.of());
        JwtFacade facade = new JwtFacade(config(JWSAlgorithm.RS256), signing);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> facade.parse(token));
        assertTrue(ex.getCause().getMessage().contains("Invalid JWT signature"));
    }

    @Test
    void unmatchedKidFallsBackToTrustedKeys() throws Exception {
        // 由 secret 生成的 HMAC 密钥每个实例 kid 随机，不同实例间仍需互通
        JwtConfig config = config(JWSAlgorithm.HS256);
        config.setSecret("shared-secret");
        String token = new JwtFacade(config).generate("u", Map.of());
        assertEquals("u", new JwtFacade(config).parse(token).getSubject());
    }

    @Test
    void forgedKidPointingToOtherKeyFails() throws Exception {
        RSAKey a = new RSAKeyGenerator(2048).keyID("a").generate();
        RSAKey b = new RSAKeyGenerator(2048).keyID("b").generate();
        RSAKey attacker = new RSAKeyGenerator(2048).keyID("a").generate();
        String forged = new JwtFacade(config(JWSAlgorithm.RS256), attacker).generate("u", Map.of());
        JwtFacade facade = new JwtFacade(config(JWSAlgorithm.RS256), a, List.of(b.toPublicJWK()));
        assertThrows(IllegalArgumentException.class, () -> facade.parse(forged));
    }

    @Test
    void tokensWithoutKidTryAllKeys() throws Exception {
        RSAKey oldKey = new RSAKeyGenerator(2048).generate(); // 无 kid
        RSAKey newKey = new RSAKeyGenerator(2048).keyID("new").generate();
        String legacy = new JwtFacade(config(JWSAlgorithm.RS256), oldKey).generate("legacy", Map.of());
        assertNull(SignedJWT.parse(legacy).getHeader().getKeyID());

        // 签名时没有 kid 的旧 token：验证密钥补 thumbprint kid 后仍可按顺序尝试
        JwtFacade facade = new JwtFacade(config(JWSAlgorithm.RS256), newKey, List.of(oldKey.toPublicJWK()));
        assertEquals("legacy", facade.parse(legacy).getSubject());
    }

    @Test
    void verificationKeyWithoutKidMatchesThumbprintKid() throws Exception {
        RSAKey oldKey = new RSAKeyGenerator(2048).generate();
        RSAKey oldWithThumbprint = new RSAKey.Builder(oldKey).keyID(oldKey.computeThumbprint().toString()).build();
        String oldToken = new JwtFacade(config(JWSAlgorithm.RS256), oldWithThumbprint).generate("u", Map.of());
        JwtFacade facade = new JwtFacade(config(JWSAlgorithm.RS256),
                new RSAKeyGenerator(2048).keyID("new").generate(), List.of(oldKey.toPublicJWK()));
        assertEquals("u", facade.parse(oldToken).getSubject());
    }

    @Test
    void jwkSetContainsSigningAndVerificationPublicKeys() throws Exception {
        ECKey oldKey = new ECKeyGenerator(Curve.P_256).keyID("old").generate();
        ECKey newKey = new ECKeyGenerator(Curve.P_256).keyID("new").generate();
        JwtFacade facade = new JwtFacade(config(JWSAlgorithm.ES256), newKey, List.of(oldKey));

        JWKSet set = JWKSet.parse(facade.getPublicJwkSet());
        assertEquals(List.of("new", "old"), set.getKeys().stream().map(JWK::getKeyID).toList());
        assertTrue(set.getKeys().stream().noneMatch(JWK::isPrivate));
        assertEquals("new", JWK.parse(facade.getPublicJwk()).getKeyID());
    }

    @Test
    void hmacRotation() throws Exception {
        OctetSequenceKey oldKey = new OctetSequenceKeyGenerator(256).keyID("old").generate();
        OctetSequenceKey newKey = new OctetSequenceKeyGenerator(256).keyID("new").generate();
        String oldToken = new JwtFacade(config(JWSAlgorithm.HS256), oldKey).generate("u", Map.of());
        JwtFacade facade = new JwtFacade(config(JWSAlgorithm.HS256), newKey, List.of(oldKey));
        assertEquals("u", facade.parse(oldToken).getSubject());
        assertThrows(UnsupportedOperationException.class, facade::getPublicJwkSet);
    }

    @Test
    void signingKeyRepeatedInVerificationKeysIsIgnored() throws Exception {
        RSAKey key = new RSAKeyGenerator(2048).keyID("k").generate();
        JwtFacade facade = new JwtFacade(config(JWSAlgorithm.RS256), key, List.of(key.toPublicJWK()));
        assertEquals(1, JWKSet.parse(facade.getPublicJwkSet()).getKeys().size());
        assertEquals("u", facade.parse(facade.generate("u", Map.of())).getSubject());
    }

    @Test
    void rejectsDuplicateKidAndIncompatibleKeys() throws Exception {
        RSAKey signing = new RSAKeyGenerator(2048).keyID("k").generate();
        RSAKey sameKid = new RSAKeyGenerator(2048).keyID("k").generate();
        assertThrows(IllegalArgumentException.class,
                () -> new JwtFacade(config(JWSAlgorithm.RS256), signing, List.of(sameKid.toPublicJWK())));

        ECKey ec = new ECKeyGenerator(Curve.P_256).keyID("ec").generate();
        assertThrows(IllegalArgumentException.class,
                () -> new JwtFacade(config(JWSAlgorithm.RS256), signing, List.of(ec.toPublicJWK())));

        ECKey p384 = new ECKeyGenerator(Curve.P_384).keyID("p384").generate();
        assertThrows(IllegalArgumentException.class,
                () -> new JwtFacade(config(JWSAlgorithm.ES256), ec, List.of(p384.toPublicJWK())));
    }
}
