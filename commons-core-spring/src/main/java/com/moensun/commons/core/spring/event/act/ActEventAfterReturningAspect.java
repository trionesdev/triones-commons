package com.moensun.commons.core.spring.event.act;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Objects;

@RequiredArgsConstructor
@Aspect
public class ActEventAfterReturningAspect implements ApplicationContextAware {

    private BeanResolver beanResolver;

    @Pointcut("@annotation(ActEventAfterReturning)")
    public void actEventAfterReturning() {
    }

    @AfterReturning(value = "actEventAfterReturning()", returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        ActEventAfterReturning actEventAfterReturning = AnnotationUtils.getAnnotation(methodSignature.getMethod(), ActEventAfterReturning.class);
        if (Objects.isNull(actEventAfterReturning)) {
            return;
        }
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new ActEventEvaluationContext(joinPoint, methodSignature.getMethod(), joinPoint.getArgs(), new DefaultParameterNameDiscoverer());
        context.setBeanResolver(this.beanResolver);
        context.setVariable("returnValue", returnValue);
        parser.parseExpression(actEventAfterReturning.value()).getValue(context);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanResolver = new BeanFactoryResolver(applicationContext);
    }
}
