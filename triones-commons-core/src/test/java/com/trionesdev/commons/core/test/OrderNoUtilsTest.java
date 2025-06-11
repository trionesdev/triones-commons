package com.trionesdev.commons.core.test;

import com.trionesdev.commons.core.util.OrderNoUtils;
import org.junit.jupiter.api.Test;

public class OrderNoUtilsTest {

    @Test
    public void test() {
        OrderNoUtils.setDefaultRandomDigits(8);
        System.out.println("Timestamp: " + OrderNoUtils.generateTimestampOrderNo());
        System.out.println("Timestamp(4): " + OrderNoUtils.generateTimestampOrderNo(4));
        System.out.println("Prefixed(ORD,6,20): " + OrderNoUtils.generatePrefixedOrderNo("ORD", 6, 20));
        System.out.println("UUID(16): " + OrderNoUtils.generateUuidOrderNo(16));
        System.out.println("Snowflake(18): " + OrderNoUtils.generateSnowflakeOrderNo(18));
        System.out.println("MD5(32): " + OrderNoUtils.generateMd5OrderNo(32));
    }
}
