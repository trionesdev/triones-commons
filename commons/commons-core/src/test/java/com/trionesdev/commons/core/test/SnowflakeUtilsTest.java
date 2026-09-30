package com.trionesdev.commons.core.test;

import com.trionesdev.commons.core.util.SnowflakeUtils;
import org.junit.jupiter.api.Test;

public class SnowflakeUtilsTest {
    @Test
    public void test() {
        SnowflakeUtils idWorker = new SnowflakeUtils(1, 1);
        System.out.println("Generating 10 Snowflake IDs...");
        for (int i = 0; i < 10; i++) {
            long id = idWorker.nextId();
            System.out.println(id);
        }

        System.out.println("\nTesting ID generation speed...");
        long startTime = System.nanoTime();
        int numberOfIds = 1000000;
        for (int i = 0; i < numberOfIds; i++) {
            idWorker.nextId();
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; // 转换为毫秒
        System.out.printf("Generated %d IDs in %d ms.\n", numberOfIds, duration);
        System.out.printf("Speed: %.2f IDs/ms\n", (double) numberOfIds / duration);
    }
}
