package com.moensun.commons.core.spring.event.act;

import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActEventHandler {
    String value() default "";
    String subject() default "";
    String actionMethod() default "";
    String action() default "";
}
