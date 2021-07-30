package com.moensun.commons.core.spring.context;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContextSetter implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        SpringContextHolder.setApplicationContext(configurableApplicationContext);
    }
}
