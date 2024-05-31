package com.trionesdev.commons.core.spring.context;

import lombok.Getter;
import org.springframework.context.ApplicationContext;

public class SpringContextHolder {
    private SpringContextHolder() {
    }

    @Getter
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }
}
