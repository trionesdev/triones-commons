package com.moensun.commons.core.spring.event.act;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.util.Objects;

@RequiredArgsConstructor
@Aspect
public class ActEventAspect {

    @Resource
    @Lazy
    private ActEventHandlerFactory actEventHandlerFactory;

    @Pointcut("@annotation(ActEvent)")
    public void actorLogAspect() {
    }

    @Before(value = "actorLogAspect()")
    public void before(JoinPoint joinPoint) {
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        if (Objects.nonNull(actorLog)) {
            AbsActEventHandler actEventHandler = actEventHandlerFactory.get(actorLog);
            if (Objects.nonNull(actEventHandler)) {
                actEventHandler.before(actorLog, joinPoint);
            }
        }
    }

    @After(value = "actorLogAspect()")
    public void after(JoinPoint joinPoint) {
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        if (Objects.nonNull(actorLog)) {
            AbsActEventHandler actEventHandler = actEventHandlerFactory.get(actorLog);
            if (Objects.nonNull(actEventHandler)) {
                actEventHandler.after(actorLog, joinPoint);
            }
        }
    }

    @Around(value = "actorLogAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        AbsActEventHandler actEventHandler = null;
        if (Objects.nonNull(actorLog)) {
            actEventHandler = actEventHandlerFactory.get(actorLog);
        }
        Object previousData = null;
        if (Objects.nonNull(actorLog)) {
            if (Objects.nonNull(actEventHandler)) {
                previousData = actEventHandler.aroundBefore(actorLog, joinPoint);
            }
        }
        Object result = joinPoint.proceed();
        if (Objects.nonNull(actorLog)) {
            if (Objects.nonNull(actEventHandler)) {
                actEventHandler.aroundAfter(actorLog, previousData, result, joinPoint);
            }
        }
        return result;
    }

    @AfterReturning(value = "actorLogAspect()", returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        if (Objects.nonNull(actorLog)) {
            AbsActEventHandler actEventHandler = actEventHandlerFactory.get(actorLog);
            if (Objects.nonNull(actEventHandler)) {
                actEventHandler.afterRetuning(actorLog, returnValue, joinPoint);
            }
        }
    }

    @AfterThrowing(value = "actorLogAspect()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        if (Objects.nonNull(actorLog)) {
            AbsActEventHandler actEventHandler = actEventHandlerFactory.get(actorLog);
            if (Objects.nonNull(actEventHandler)) {
                actEventHandler.afterThrowing(actorLog, ex, joinPoint);
            }
        }
    }

    private ActEvent getMethodAnnotation(JoinPoint joinPoint) {
        if (Objects.isNull(actEventHandlerFactory)) {
            return null;
        }
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return AnnotationUtils.getAnnotation(methodSignature.getMethod(), ActEvent.class);
    }
}
