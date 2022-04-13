package com.moensun.commons.security.spring.jwt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.moensun.commons.context.actor.Actor;
import com.moensun.commons.context.actor.ActorContext;
import com.moensun.commons.core.jwt.JwtFacade;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.moensun.commons.core.jwt.ClaimsKeyConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenConfig jwtTokenConfig;
    private final JwtFacade jwtFacade;
    private final ActorContext actorContext;

    private final String JWT_TOKEN_URI = "jwt/token";

    public JwtAuthenticationFilter(JwtTokenConfig jwtTokenConfig, JwtFacade jwtFacade, ActorContext actorContext) {
        this.jwtTokenConfig = jwtTokenConfig;
        this.jwtFacade = jwtFacade;
        this.actorContext = actorContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);
        if (StringUtils.isNotBlank(authorization)) {
            authorization = authorization.replace("Bearer ", "");
        }
        //region jwt token 解析逻辑
        if (BooleanUtils.isTrue(jwtTokenConfig.getLocal()) && StringUtils.isNotBlank(authorization)) {
            Pattern pattern = Pattern.compile(JWT_TOKEN_URI);
            Matcher matcher = pattern.matcher(request.getRequestURI());
            if (matcher.matches()) {
                response.setContentType(APPLICATION_JSON_VALUE);
                response.getWriter().write(JSON.toJSONString(jwtFacade.parse(authorization)));
                return;
            }
        }
        //endregion

        Actor actor = new Actor();
        actor.setTime(Instant.now());
        if (StringUtils.isNotBlank(authorization)) {
            Map<String, Object> claims = null;
            if (BooleanUtils.isTrue(jwtTokenConfig.getLocal())) {
                claims = jwtFacade.parse(authorization);
            } else {
                claims = remoteClaims(authorization);
            }
            if (Objects.nonNull(claims)) {
                String actorId = String.valueOf(claims.get(ACTOR_ID));
                String role = String.valueOf(claims.get(ACTOR_ROLE));
                String tenantId = String.valueOf(claims.get(ACTOR_TENANT_ID));
                if (Objects.nonNull(actorId) && Objects.nonNull(role)) {
                    actor.setActorId(actorId);
                    actor.setRole(role);
                    actor.setTenantId(tenantId);
                    JwtUserDetails userDetails =
                            JwtUserDetails.builder().token(authorization).actorId(actorId).role(role).tenantId(tenantId)
                                    .build();
                    List<SimpleGrantedAuthority> authorities = Lists.newArrayList(new SimpleGrantedAuthority(role));
                    Authentication authentication =
                            new JwtAuthenticationToken(userDetails.getActorId(), userDetails, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        actorContext.setActor(actor);
        filterChain.doFilter(request, response);
        actorContext.resetActor();
    }


    public Map<String,Object> remoteClaims(String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(jwtTokenConfig.getEndpoint()+"/"+JWT_TOKEN_URI)
                .header(AUTHORIZATION,token)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return JSON.parseObject(response.body().string(), new TypeReference<Map<String, Object>>(){});
        }
    }
}
