package com.moensun.commons.core.spring.event.act;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.util.Objects;

@Aspect
public class ActEventAspect {

    @Resource
    @Lazy
    private ActEventHandler actEventHandler;

    @Pointcut("@annotation(ActEvent)")
    public void actorLogAspect() {
    }

    @Before(value = "actorLogAspect()")
    public void before(JoinPoint joinPoint){
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        if(Objects.nonNull(actorLog)){
            actEventHandler.before(actorLog, joinPoint.getArgs());
        }
    }

    @After(value = "actorLogAspect()")
    public void after(JoinPoint joinPoint){
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        if(Objects.nonNull(actorLog)){
            actEventHandler.after(actorLog, joinPoint.getArgs());
        }
    }

    @Around(value = "actorLogAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        Object previousData = null;
        if(Objects.nonNull(actorLog)){
            previousData = actEventHandler.aroundBefore(actorLog, joinPoint.getArgs());
        }
        Object result =  joinPoint.proceed();
        if(Objects.nonNull(actorLog)){
            actEventHandler.aroundAfter(actorLog, joinPoint.getArgs(),previousData, result);
        }
        return result;
    }

    @AfterReturning(value = "actorLogAspect()",returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue){
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        if(Objects.nonNull(actorLog)){
            actEventHandler.afterRetuning(actorLog,joinPoint.getArgs(),returnValue);
        }
    }

    @AfterThrowing(value = "actorLogAspect()",throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex){
        ActEvent actorLog = getMethodAnnotation(joinPoint);
        if(Objects.nonNull(actorLog)){
            actEventHandler.afterRetuning(actorLog, joinPoint.getArgs(), ex);
        }
    }

    private ActEvent getMethodAnnotation(JoinPoint joinPoint){
        if(Objects.isNull(actEventHandler)){
            return null;
        }
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        return AnnotationUtils.getAnnotation(methodSignature.getMethod(), ActEvent.class);
    }
}
