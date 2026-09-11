package com.trionesdev.commons.core.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 订单序列号工具类
 */
public class OrderNoUtils {

    private static final String TIMESTAMP_PATTERN = "yyyyMMddHHmmssSSS";
    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private static final SnowflakeUtil SNOWFLAKE = new SnowflakeUtil(1, 1);
    private static final ThreadLocal<MessageDigest> MD5 = ThreadLocal.withInitial(OrderNoUtils::createMd5);

    /**
     * 默认随机位数
     */
    private static volatile int defaultRandomDigits = 6;

    private OrderNoUtils() {
    }

    /**
     * 设置默认随机位数
     */
    public static void setDefaultRandomDigits(int digits) {
        if (digits < 0) {
            throw new IllegalArgumentException("随机位数不能为负");
        }
        defaultRandomDigits = digits;
    }

    /**
     * 生成时间戳订单号，使用默认随机位数。
     *
     * @return yyyyMMddHHmmssSSS + 默认随机数字
     */
    public static String generateTimestampOrderNo() {
        return generateTimestampOrderNo(defaultRandomDigits);
    }

    /**
     * 生成时间戳订单号，并可指定随机位数。
     *
     * @param randomDigits 随机数字长度
     * @return yyyyMMddHHmmssSSS + 随机数字
     */
    public static String generateTimestampOrderNo(int randomDigits) {
        String timestamp = DateFormatUtils.format(System.currentTimeMillis(), TIMESTAMP_PATTERN);
        return timestamp + RandomStringUtils.randomNumeric(randomDigits);
    }

    /**
     * 生成带前缀的订单号，并可指定随机位数和总长度，超出截断，不足补随机数。
     *
     * @param prefix       前缀
     * @param randomDigits 随机数字长度
     * @param totalLength  目标总长度
     * @return prefix + yyyyMMdd + 随机数字，按 totalLength 调整
     */
    public static String generatePrefixedOrderNo(String prefix, int randomDigits, int totalLength) {
        prefix = StringUtils.defaultIfBlank(prefix, "");
        String date = DateFormatUtils.format(System.currentTimeMillis(), DATE_PATTERN);
        return adjustLength(prefix + date + RandomStringUtils.randomNumeric(randomDigits), totalLength);
    }

    /**
     * 生成 UUID 订单号，可指定输出长度
     *
     * @param length 目标长度，截断或补随机字符
     */
    public static String generateUuidOrderNo(int length) {
        return adjustLength(UUID.randomUUID().toString().replace("-", ""), length);
    }

    /**
     * 生成 Snowflake 算法订单号，可指定输出长度
     *
     * @param length 目标长度
     */
    public static String generateSnowflakeOrderNo(int length) {
        return adjustLength(String.valueOf(SNOWFLAKE.nextId()), length);
    }

    /**
     * 生成 MD5 订单号，可指定输出长度
     * 基于当前时间戳和随机字符串，使用 Java 原生 MessageDigest
     */
    public static String generateMd5OrderNo(int length) {
        String raw = System.currentTimeMillis() + RandomStringUtils.randomAlphanumeric(defaultRandomDigits);
        MessageDigest md = MD5.get();
        md.reset();
        return adjustLength(toHex(md.digest(raw.getBytes(StandardCharsets.UTF_8))), length);
    }

    /**
     * 调整字符串长度，超长则截断，不足则补随机数字
     */
    private static String adjustLength(String source, int targetLength) {
        if (targetLength <= 0) {
            throw new IllegalArgumentException("目标长度必须大于 0");
        }
        int length = source.length();
        if (length == targetLength) {
            return source;
        }
        if (length > targetLength) {
            return source.substring(0, targetLength);
        }
        return source + RandomStringUtils.randomNumeric(targetLength - length);
    }

    private static MessageDigest createMd5() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5算法不可用", e);
        }
    }

    private static String toHex(byte[] bytes) {
        char[] chars = new char[bytes.length << 1];
        for (int i = 0, j = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            chars[j++] = HEX_DIGITS[v >>> 4];
            chars[j++] = HEX_DIGITS[v & 0x0F];
        }
        return new String(chars);
    }
}
