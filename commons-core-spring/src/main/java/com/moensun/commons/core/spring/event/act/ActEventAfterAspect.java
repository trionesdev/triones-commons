package com.moensun.commons.core.spring.event.act;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
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
public class ActEventAfterAspect implements ApplicationContextAware {

    private BeanResolver beanResolver;

    @Pointcut("@annotation(ActEventAfter)")
    public void actEventAfter() {
    }

    @After(value = "actEventAfter()")
    public void after(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        ActEventAfter actEventAfter = AnnotationUtils.getAnnotation(methodSignature.getMethod(), ActEventAfter.class);
        if (Objects.isNull(actEventAfter)) {
            return;
        }
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new ActEventEvaluationContext(joinPoint, methodSignature.getMethod(), joinPoint.getArgs(), new DefaultParameterNameDiscoverer());
        context.setBeanResolver(this.beanResolver);
        parser.parseExpression(actEventAfter.value()).getValue(context);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanResolver = new BeanFactoryResolver(applicationContext);
    }
}
