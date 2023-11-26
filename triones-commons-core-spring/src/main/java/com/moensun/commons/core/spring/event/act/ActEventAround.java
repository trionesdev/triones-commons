package com.moensun.commons.core.spring.event.act;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ActEventAround {
    /**
     * @return the Spring-EL expression to be evaluated after invoking the protected
     * spel response is beforeData as a visible in evaluation context
     * method
     */
    @Language(value = "SpEL")
    String before() default "";

    /**
     * @return the Spring-EL expression to be evaluated after invoking the protected
     * method
     */
    @Language(value = "SpEL")
    String after();
}
