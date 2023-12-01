package com.trionesdev.commons.core.spring.event.act;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ActEventBefore {
    /**
     * @return the Spring-EL expression to be evaluated after invoking the protected
     * method
     */
    @Language(value = "SpEL")
    String value();
}
