package com.moensun.commons.core.spring.permission.act;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ActPermission {

    /**
     * @return the Spring-EL expression to be evaluated after invoking the protected
     * method
     */
    String value();
}
