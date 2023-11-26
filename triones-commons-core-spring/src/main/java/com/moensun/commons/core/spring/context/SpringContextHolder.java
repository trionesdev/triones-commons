package com.moensun.commons.core.spring.context;

import org.springframework.context.ApplicationContext;

public class SpringContextHolder {
    private SpringContextHolder() {
    }

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }
}
