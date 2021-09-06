package com.moensun.commons.core.operator;

import com.alibaba.ttl.TransmittableThreadLocal;

public class OperateContextHolder {
    private OperateContextHolder() {
    }

    private static final TransmittableThreadLocal<Operator> operateHolder = new TransmittableThreadLocal<>();

    public static void resetOperator() {
        operateHolder.remove();
    }

    public static void setOperator(Operator operator) {
        if (operator == null) {
            resetOperator();
        } else {
            operateHolder.set(operator);
        }
    }

    public static Operator getOperator() {
        return operateHolder.get();
    }



}
