package com.moensun.commons.core.spring.permission.act;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ActPermission {

    // language=SpEL
    @Language(value = "SpEL")
    String value();

    String description() default "";
}
