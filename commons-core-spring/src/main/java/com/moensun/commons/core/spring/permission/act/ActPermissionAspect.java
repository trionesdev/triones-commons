package com.moensun.commons.core.spring.permission.act;

import com.moensun.commons.exception.PermissionDeniedException;
import org.apache.commons.lang3.BooleanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

@Aspect
public class ActPermissionAspect implements ApplicationContextAware {

    private BeanResolver beanResolver;

    @Pointcut(value = "@annotation(ActPermission)")
    public void actPermissionAspect() {
    }

    @Before(value = "actPermissionAspect()")
    public void before(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        ActPermission actPermission = AnnotationUtils.getAnnotation(methodSignature.getMethod(), ActPermission.class);
        if (Objects.isNull(actPermission)) {
            return;
        }
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new ActPermissionEvaluationContext(joinPoint.getTarget(), methodSignature.getMethod(), joinPoint.getArgs(), new DefaultParameterNameDiscoverer());
        context.setBeanResolver(this.beanResolver);
        Boolean result = parser.parseExpression(actPermission.value()).getValue(context, Boolean.class);
        if (BooleanUtils.isNotTrue(result)) {
            throw new PermissionDeniedException();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanResolver = new BeanFactoryResolver(applicationContext);
    }
}
