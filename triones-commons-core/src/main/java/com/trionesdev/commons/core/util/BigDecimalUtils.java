package com.trionesdev.commons.core.util;

import java.math.BigDecimal;
import java.util.Objects;

public class BigDecimalUtils {

    /**
     * 加
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal add(BigDecimal a,BigDecimal b){
        BigDecimal scopeA = a;
        BigDecimal scopeB = b;
        if(Objects.isNull(scopeA)){
            scopeA = BigDecimal.ZERO;
        }
        if(Objects.isNull(scopeB)){
            scopeB = BigDecimal.ZERO;
        }
        return scopeA.add(scopeB);
    }

    /**
     * 减
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal subtract(BigDecimal a,BigDecimal b){
        BigDecimal scopeA = a;
        BigDecimal scopeB = b;
        if(Objects.isNull(scopeA)){
            scopeA = BigDecimal.ZERO;
        }
        if(Objects.isNull(scopeB)){
            scopeB = BigDecimal.ZERO;
        }
        return scopeA.subtract(scopeB);
    }

    /**
     * 乘
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal multiply(BigDecimal a,BigDecimal b){
        BigDecimal scopeA = a;
        BigDecimal scopeB = b;
        if(Objects.isNull(scopeA)){
            scopeA = BigDecimal.ZERO;
        }
        if(Objects.isNull(scopeB)){
            scopeB = BigDecimal.ZERO;
        }
        return scopeA.multiply(scopeB);
    }

    /**
     * 除
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal divide(BigDecimal a,BigDecimal b){
        BigDecimal scopeA = a;
        BigDecimal scopeB = b;
        if(Objects.isNull(scopeA)){
            scopeA = BigDecimal.ZERO;
        }
        if(Objects.isNull(scopeB)){
            scopeB = BigDecimal.ZERO;
        }
        return scopeA.divide(scopeB);
    }
}
