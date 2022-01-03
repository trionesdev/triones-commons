package com.moensun.commons.core.spring.event.act;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ActEventAfterThrowing {
    /**
     * @return the Spring-EL expression to be evaluated after invoking the protected
     * method
     */
    String value();
}
