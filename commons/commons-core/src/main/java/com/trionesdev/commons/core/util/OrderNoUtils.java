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
 * 集成 Apache Commons Lang3
 * 实现灵活长度配置
 */
public class OrderNoUtils {
    /**
     * 默认随机位数
     */
    private static int defaultRandomDigits = 6;

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
        String timestamp = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
        String rand = RandomStringUtils.randomNumeric(randomDigits);
        return timestamp + rand;
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
        if (StringUtils.isBlank(prefix)) {
            prefix = "";
        }
        String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
        String base = prefix + date + RandomStringUtils.randomNumeric(randomDigits);
        return adjustLength(base, totalLength);
    }

    /**
     * 生成 UUID 订单号，可指定输出长度
     *
     * @param length 目标长度，截断或补随机字符
     */
    public static String generateUuidOrderNo(int length) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return adjustLength(uuid, length);
    }

    /**
     * 生成 Snowflake 算法订单号，可指定输出长度
     *
     * @param length 目标长度
     */
    public static String generateSnowflakeOrderNo(int length) {
        SnowflakeUtil idWorker = new SnowflakeUtil(1, 1);
        String id = String.valueOf(idWorker.nextId());
        return adjustLength(id, length);
    }

    /**
     * 生成 MD5 订单号，可指定输出长度
     * 基于当前时间戳和随机字符串，使用 Java 原生 MessageDigest
     */
    public static String generateMd5OrderNo(int length) {
        try {
            String raw = System.currentTimeMillis() + RandomStringUtils.randomAlphanumeric(defaultRandomDigits);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return adjustLength(sb.toString(), length);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    /**
     * 调整字符串长度，超长则截断，不足则补随机数字
     */
    private static String adjustLength(String source, int targetLength) {
        if (targetLength <= 0) {
            throw new IllegalArgumentException("目标长度必须大于 0");
        }
        if (source.length() == targetLength) {
            return source;
        }
        if (source.length() > targetLength) {
            return source.substring(0, targetLength);
        }
        // 长度不足，补随机数字
        int diff = targetLength - source.length();
        return source + RandomStringUtils.randomNumeric(diff);
    }
}
