package com.moensun.commons.core.spring.event.act;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActEvent {
    String subject() default "";
    String actionMethod() default "";
    String action() default "";
    String description()  default "";
}
