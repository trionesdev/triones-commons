package com.moensun.commons.security.spring.jwt;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

public class JwtServerConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<JwtServerConfigurer<H>, H> {
    private final ApplicationContext context;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public JwtServerConfigurer(ApplicationContext context, JwtAuthenticationFilter jwtAuthenticationFilter){
        this.context = context;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    public void init(H builder) throws Exception {
        super.init(builder);
    }

    @Override
    public void configure(H builder) {
        builder.addFilterAfter(jwtAuthenticationFilter, SecurityContextHolderFilter.class);
    }
}
