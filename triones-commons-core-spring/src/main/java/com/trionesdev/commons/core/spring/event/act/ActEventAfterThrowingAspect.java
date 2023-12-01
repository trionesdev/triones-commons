package com.trionesdev.commons.core.spring.event.act;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Objects;

@RequiredArgsConstructor
@Aspect
public class ActEventAfterThrowingAspect extends ActEventAspect {

    @Pointcut("@annotation(com.trionesdev.commons.core.spring.event.act.ActEventAfterThrowing)")
    public void actEventAfterThrowing() {
    }

    @AfterThrowing(value = "actEventAfterThrowing()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        ActEventAfterReturning actEventAfterReturning = AnnotationUtils.getAnnotation(methodSignature.getMethod(), ActEventAfterReturning.class);
        if (Objects.isNull(actEventAfterReturning)) {
            return;
        }
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new ActEventEvaluationContext(joinPoint.getTarget(), methodSignature.getMethod(), joinPoint.getArgs(), new DefaultParameterNameDiscoverer());
        context.setBeanResolver(this.beanResolver);
        context.setVariable("ex", ex);
        parser.parseExpression(actEventAfterReturning.value()).getValue(context);
    }

}
