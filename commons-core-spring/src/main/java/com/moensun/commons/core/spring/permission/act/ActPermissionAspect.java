package com.moensun.commons.core.spring.permission.act;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@RequiredArgsConstructor
@Aspect
public class ActPermissionAspect {

    @Pointcut("@annotation(ActPermission)")
    public void actPermissionAspect() {
    }

}
