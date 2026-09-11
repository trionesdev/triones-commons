package com.trionesdev.commons.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtils {

    /**
     * 加，null 按 0 处理
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return nz(a).add(nz(b));
    }

    /**
     * 减，null 按 0 处理
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return nz(a).subtract(nz(b));
    }

    /**
     * 乘，null 按 0 处理
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return nz(a).multiply(nz(b));
    }

    /**
     * 除，默认保留 2 位小数并四舍五入；null 按 0 处理
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        return divide(a, b, 2, RoundingMode.HALF_UP);
    }

    /**
     * 除，null 按 0 处理
     *
     * @param a
     * @param b
     * @param scale
     * @param roundingMode
     * @return
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale, RoundingMode roundingMode) {
        return nz(a).divide(nz(b), scale, roundingMode);
    }

    /**
     * null 转 0
     *
     * @param value
     * @return
     */
    public static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
