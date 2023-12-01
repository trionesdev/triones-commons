package com.trionesdev.commons.security.spring.jwt.reactive;

import com.google.common.collect.Lists;
import com.trionesdev.commons.context.actor.Actor;
import com.trionesdev.commons.security.spring.jwt.JwtAuthenticationToken;
import com.trionesdev.commons.security.spring.jwt.JwtUserDetails;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

public class JwtServerSecurityContextRepository implements ServerSecurityContextRepository {

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        return Mono.deferContextual(contextView -> {
            try {
                Actor actor = contextView.get(Actor.class);
                JwtUserDetails userDetails =
                        JwtUserDetails.builder().actorId(actor.getActorId()).role(actor.getRole()).tenantId(actor.getTenantId()).tenantMemberId(actor.getTenantMemberId())
                                .build();
                List<SimpleGrantedAuthority> authorities = Lists.newArrayList(new SimpleGrantedAuthority(actor.getRole()));
                Authentication authentication = new JwtAuthenticationToken(userDetails.getActorId(), userDetails, authorities);
                return new ReactiveAuthenticationManager() {
                    @Override
                    public Mono<Authentication> authenticate(Authentication authentication) {
                        return Mono.just(authentication);
                    }
                }.authenticate(authentication).map(SecurityContextImpl::new);
            } catch (NoSuchElementException ex) {
                return Mono.empty();
            }
        });


    }

//    public Map<String,Object> remoteClaims(String token) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(jwtTokenConfig.getEndpoint()+"/"+JWT_TOKEN_URI)
//                .header(AUTHORIZATION,token)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            return JSON.parseObject(response.body().string(), new TypeReference<Map<String, Object>>(){});
//        }
//    }

}
