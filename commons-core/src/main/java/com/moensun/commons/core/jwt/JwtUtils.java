package com.moensun.commons.core.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public final class JwtUtils {

    @SneakyThrows
    public static String generateToken(JwtConfig jwtConfig, String subject, Map<String, Object> claims) {
        Date issueAt = new Date();

        JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder()
                .subject(subject)
                .issueTime(issueAt);
        if(MapUtils.isNotEmpty(claims)){
            claims.forEach(jwtClaimsSetBuilder::claim);
        }
        if (jwtConfig.getExpiration() > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(issueAt);
            calendar.add(Calendar.SECOND, jwtConfig.getExpiration());
            jwtClaimsSetBuilder.expirationTime(calendar.getTime());
        }
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSetBuilder.build());
        signedJWT.sign(new MACSigner(jwtConfig.getSecret()));
        return signedJWT.serialize();
    }

    @SneakyThrows
    public static Map<String,Object> parseClaims(JwtConfig jwtConfig, String token) {
        JWSVerifier verifier = new MACVerifier(jwtConfig.getSecret());
        SignedJWT signedJWT = SignedJWT.parse(token);
        if(!signedJWT.verify(verifier)){
            return null;
        }
        return signedJWT.getJWTClaimsSet().getClaims();
    }
}
