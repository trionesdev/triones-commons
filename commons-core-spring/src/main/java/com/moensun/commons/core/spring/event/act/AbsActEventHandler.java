package com.moensun.commons.core.spring.event.act;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public abstract class AbsActEventHandler {
    public void before(ActEvent actEvent, JoinPoint joinPoint) {
    }

    public void after(ActEvent actEvent, JoinPoint joinPoint) {
    }

    public Object aroundBefore(ActEvent actEvent, ProceedingJoinPoint joinPoint) {
        return null;
    }

    public void aroundAfter(ActEvent actEvent, Object aroundBeforeResult, Object result, ProceedingJoinPoint joinPoint) {
    }

    public void afterRetuning(ActEvent actEvent, Object result, JoinPoint joinPoint) {
    }

    public void afterThrowing(ActEvent actEvent, Exception ex, JoinPoint joinPoint) {
    }

    public ActEvent getMethodAnnotation(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return AnnotationUtils.getAnnotation(methodSignature.getMethod(), ActEvent.class);
    }

    protected Object getObjectId(ActEvent actEvent, JoinPoint joinPoint) {
        if (StringUtils.isBlank(actEvent.objectId())) {
            return null;
        }
        if (StringUtils.containsNone(actEvent.objectId(), "#")) {
            return actEvent.objectId();
        }
        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(actEvent.objectId()).getValue(evaluationContext(joinPoint));
    }

    protected EvaluationContext evaluationContext(JoinPoint joinPoint) {
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return context;
    }
}
