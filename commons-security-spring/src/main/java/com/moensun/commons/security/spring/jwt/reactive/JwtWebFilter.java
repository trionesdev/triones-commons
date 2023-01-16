package com.moensun.commons.security.spring.jwt.reactive;

import com.moensun.commons.context.actor.Actor;
import com.moensun.commons.context.actor.reactive.ActorContext;
import com.moensun.commons.core.jwt.JwtFacade;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import static com.moensun.commons.core.jwt.ClaimsKeyConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class JwtWebFilter implements WebFilter {
    private final Tracer tracer;
    private final ActorContext actorContext;
    private final JwtFacade jwtFacade;

    public JwtWebFilter(Tracer tracer, JwtFacade jwtFacade, ActorContext actorContext) {
        this.tracer = tracer;
        this.jwtFacade = jwtFacade;
        this.actorContext = actorContext;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authorization = request.getHeaders().getFirst(AUTHORIZATION);

        Actor actor = null;
        if (StringUtils.isNotBlank(authorization)) {
            authorization = authorization.replace("Bearer ", "");
            try {
                actor = new Actor();
                actor.setTime(Instant.now());
                Map<String, Object> claims = null;
                claims = jwtFacade.parse(authorization);
                if (Objects.nonNull(claims)) {
                    String actorId = String.valueOf(claims.get(ACTOR_ID));
                    String role = String.valueOf(claims.get(ACTOR_ROLE));
                    String tenantId = String.valueOf(claims.get(ACTOR_TENANT_ID));

                    actor.setActorId(actorId);
                    actor.setRole(role);
                    actor.setTenantId(tenantId);

                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        Actor finalActor = actor;

        return chain.filter(exchange).contextWrite(context -> {
            ScopeManager scopeManager = tracer.scopeManager();
            Span parentSpan = scopeManager.activeSpan();
            Tracer.SpanBuilder spanBuilder = tracer.buildSpan("set-actor");
            Span actorSpan;
            if (Objects.nonNull(parentSpan)) {
                actorSpan = spanBuilder.asChildOf(parentSpan).start();
            } else {
                actorSpan = spanBuilder.start();
            }
            scopeManager.activate(actorSpan);
            return actorContext.setActor(context, finalActor);
        });
    }
}
