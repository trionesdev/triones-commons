package com.moensun.commons.core.spring.event.act;

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
    String before() default "";

    /**
     * @return the Spring-EL expression to be evaluated after invoking the protected
     * method
     */
    String after();
}
