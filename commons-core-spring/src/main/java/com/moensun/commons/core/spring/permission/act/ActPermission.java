package com.moensun.commons.core.spring.permission.act;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActPermission {
    String subject() default "";
}
