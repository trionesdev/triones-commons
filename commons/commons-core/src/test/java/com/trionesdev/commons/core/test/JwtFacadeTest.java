package com.trionesdev.commons.core.test;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.trionesdev.commons.core.jwt.JwtConfig;
import com.trionesdev.commons.core.jwt.JwtFacade;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtFacadeTest {
    @Test
    void exportedPublicKeysVerifyTokensWithoutExposingSecrets() throws Exception {
        for (JWSAlgorithm algorithm : new JWSAlgorithm[]{JWSAlgorithm.RS256, JWSAlgorithm.ES256}) {
            JwtFacade facade = new JwtFacade(config(algorithm));
            com.nimbusds.jose.jwk.JWKSet jwkSet = com.nimbusds.jose.jwk.JWKSet.parse(facade.getPublicJwkSet());
            assertEquals(1, jwkSet.getKeys().size());
            Map<String, Object> exported = facade.getPublicJwk();
            assertEquals(exported, jwkSet.getKeys().get(0).toJSONObject());
            com.nimbusds.jose.jwk.JWK publicKey = com.nimbusds.jose.jwk.JWK.parse(exported);
            assertFalse(publicKey.isPrivate());
            for (String field : new String[]{"d", "p", "q", "dp", "dq", "qi", "oth", "k"}) {
                assertFalse(exported.containsKey(field), field);
            }
            com.nimbusds.jwt.SignedJWT jwt = com.nimbusds.jwt.SignedJWT.parse(
                    facade.generate("user", Map.of()));
            assertEquals(jwt.getHeader().getKeyID(), publicKey.getKeyID());
            assertEquals(exported, facade.getPublicJwk());
            java.security.Key key = publicKey instanceof com.nimbusds.jose.jwk.RSAKey
                    ? ((com.nimbusds.jose.jwk.RSAKey) publicKey).toRSAPublicKey()
                    : ((com.nimbusds.jose.jwk.ECKey) publicKey).toECPublicKey();
            assertTrue(jwt.verify(new com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory()
                    .createJWSVerifier(jwt.getHeader(), key)));
        }
    }

    @Test
    void hmacCannotExportPublicKey() {
        JwtFacade facade = new JwtFacade(config(JWSAlgorithm.HS256));
        assertThrows(UnsupportedOperationException.class, facade::getPublicJwkSet);
        assertThrows(UnsupportedOperationException.class, facade::getPublicJwk);
    }

    private JwtConfig config(JWSAlgorithm algorithm) {
        return JwtConfig.builder().algorithm(algorithm).expiration(3600).build();
    }

    @Test
    void roundTripEverySupportedAlgorithm() throws Exception {
        for (JWSAlgorithm algorithm : new JWSAlgorithm[]{
                JWSAlgorithm.RS256, JWSAlgorithm.RS384, JWSAlgorithm.RS512,
                JWSAlgorithm.PS256, JWSAlgorithm.PS384, JWSAlgorithm.PS512,
                JWSAlgorithm.ES256, JWSAlgorithm.ES384, JWSAlgorithm.ES512,
                JWSAlgorithm.HS256, JWSAlgorithm.HS384, JWSAlgorithm.HS512}) {
            JwtFacade facade = new JwtFacade(config(algorithm));
            String token = facade.generate("user-1", Map.of("role", "ADMIN"));
            JWTClaimsSet claims = facade.parse(token);
            assertEquals("user-1", claims.getSubject(), algorithm.toString());
            assertEquals("ADMIN", facade.parseClaims(token).get("role"));
            assertTrue(claims.getExpirationTime().after(claims.getIssueTime()));
        }
    }

    @Test
    void sharedKeyWorksAcrossInstances() throws Exception {
        OctetSequenceKey key = new OctetSequenceKeyGenerator(512).keyID("shared").generate();
        JwtFacade issuer = new JwtFacade(config(JWSAlgorithm.HS256), key);
        JwtFacade verifier = new JwtFacade(config(JWSAlgorithm.HS256), key);
        assertEquals("user", verifier.parse(issuer.generate("user", Map.of())).getSubject());
        JwtFacade otherKey = new JwtFacade(config(JWSAlgorithm.HS256));
        assertThrows(IllegalArgumentException.class,
                () -> otherKey.parse(issuer.generate("user", Map.of())));
        JwtFacade otherAlgorithm = new JwtFacade(config(JWSAlgorithm.HS512), key);
        assertThrows(IllegalArgumentException.class,
                () -> otherAlgorithm.parse(issuer.generate("user", Map.of())));
    }

    @Test
    void rejectsInvalidTimesAndMalformedTokens() {
        JwtFacade facade = new JwtFacade(config(JWSAlgorithm.HS256));
        Instant now = Instant.now();
        JWTClaimsSet[] invalid = {
                new JWTClaimsSet.Builder().subject("missing-exp").build(),
                new JWTClaimsSet.Builder().expirationTime(Date.from(now.minusSeconds(60))).build(),
                new JWTClaimsSet.Builder().expirationTime(Date.from(now.plusSeconds(3600)))
                        .notBeforeTime(Date.from(now.plusSeconds(600))).build()
        };
        for (JWTClaimsSet claims : invalid) {
            assertThrows(IllegalArgumentException.class, () -> facade.parse(facade.generate(claims)));
        }
        assertThrows(IllegalArgumentException.class, () -> facade.parse("invalid.jwt"));
        assertThrows(NullPointerException.class, () -> facade.parse(null));
    }

    @Test
    void validatesConfiguration() throws Exception {
        com.nimbusds.jose.jwk.RSAKey rsaKey = new com.nimbusds.jose.jwk.gen.RSAKeyGenerator(2048).generate();
        assertThrows(IllegalArgumentException.class, () -> new JwtFacade(config(JWSAlgorithm.HS256), rsaKey));
        assertThrows(IllegalArgumentException.class,
                () -> new JwtFacade(config(JWSAlgorithm.RS256), rsaKey.toPublicJWK()));
        JwtConfig longSecret = config(JWSAlgorithm.HS512);
        longSecret.setSecret("x".repeat(65));
        assertThrows(IllegalArgumentException.class, () -> new JwtFacade(longSecret));
        JwtConfig noExpiration = config(JWSAlgorithm.HS256);
        noExpiration.setExpiration(0);
        JwtFacade facade = new JwtFacade(noExpiration);
        assertThrows(IllegalArgumentException.class, () -> facade.generate("user", Map.of()));
    }

    @Test
    void padsShortSecretsWithZeroBytes() {
        JWSAlgorithm[] algorithms = {JWSAlgorithm.HS256, JWSAlgorithm.HS384, JWSAlgorithm.HS512};
        int[] lengths = {32, 48, 64};
        for (int i = 0; i < algorithms.length; i++) {
            for (String secret : new String[]{"", "short", "密钥", "x".repeat(lengths[i])}) {
                JwtConfig config = config(algorithms[i]);
                config.setSecret(secret);
                JwtFacade issuer = new JwtFacade(config);
                byte[] padded = java.util.Arrays.copyOf(
                        secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), lengths[i]);
                JwtFacade verifier = new JwtFacade(config, new OctetSequenceKey.Builder(padded).build());
                assertEquals("user", verifier.parse(issuer.generate("user", Map.of())).getSubject());
            }
            JwtConfig oversized = config(algorithms[i]);
            oversized.setSecret("x".repeat(lengths[i] + 1));
            assertThrows(IllegalArgumentException.class, () -> new JwtFacade(oversized));
        }
    }
}
