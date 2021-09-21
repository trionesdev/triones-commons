package com.moensun.commons.core.util;

import java.math.BigDecimal;
import java.util.Objects;

public class BigDecimalUtils {

    public static BigDecimal add(BigDecimal a,BigDecimal b){
        if(Objects.isNull(a)){
            a = BigDecimal.ZERO;
        }
        if(Objects.isNull(b)){
            b = BigDecimal.ZERO;
        }
        return a.add(b);
    }

    public static BigDecimal subtract(BigDecimal a,BigDecimal b){
        if(Objects.isNull(a)){
            a = BigDecimal.ZERO;
        }
        if(Objects.isNull(b)){
            b = BigDecimal.ZERO;
        }
        return a.subtract(b);
    }

    public static BigDecimal multiply(BigDecimal a,BigDecimal b){
        if(Objects.isNull(a)){
            a = BigDecimal.ZERO;
        }
        if(Objects.isNull(b)){
            b = BigDecimal.ZERO;
        }
        return a.multiply(b);
    }

    public static BigDecimal divide(BigDecimal a,BigDecimal b){
        if(Objects.isNull(a)){
            a = BigDecimal.ZERO;
        }
        if(Objects.isNull(b)){
            b = BigDecimal.ZERO;
        }
        return a.divide(b);
    }
}
