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
        return zeroIfNull(a).add(zeroIfNull(b));
    }

    /**
     * 减，null 按 0 处理
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return zeroIfNull(a).subtract(zeroIfNull(b));
    }

    /**
     * 乘，null 按 0 处理
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return zeroIfNull(a).multiply(zeroIfNull(b));
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
        return zeroIfNull(a).divide(zeroIfNull(b), scale, roundingMode);
    }

    /**
     * null 转 0
     *
     * @param value
     * @return
     */
    public static BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 是否为正数
     *
     * @param value
     * @return
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 是否相等
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean equals(BigDecimal a, BigDecimal b) {
        return a != null && b != null && a.compareTo(b) == 0;
    }

    /**
     * 是否大于
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean gt(BigDecimal a, BigDecimal b) {
        return a != null && b != null && a.compareTo(b) > 0;
    }
    /**
     * 是否大于等于
     *
     * @param a
     * @param b
     * @return
     */

    public static boolean gte(BigDecimal a, BigDecimal b) {
        return a != null && b != null && a.compareTo(b) >= 0;
    }
    /**
     * 是否小于
     *
     * @param a
     * @param b
     * @return
     */

    public static boolean lt(BigDecimal a, BigDecimal b) {
        return a != null && b != null && a.compareTo(b) < 0;
    /**
     * 是否小于等于
     *
     * @param a
     * @param b
     * @return
     */
    }
    /**
     * 是否小于等于
     *
     * @param a
     * @param b
     * @return
     */

    public static boolean lte(BigDecimal a, BigDecimal b) {
        return a != null && b != null && a.compareTo(b) <= 0;
    }
}
