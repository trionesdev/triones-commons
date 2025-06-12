package com.trionesdev.commons.core.util;

/**
 * Java implementation of Twitter's Snowflake algorithm for generating unique IDs.
 *
 * <p>
 * <p>
 * Snowflake IDs are 64-bit integers composed of:
 * 1 sign bit (always 0)
 * 41 bits for timestamp in milliseconds (gives us 69 years)
 * 10 bits for a data center ID and worker ID (5 bits for each, allowing 32 data centers and 32 workers)
 * 12 bits for a sequence number (per-millisecond, allows 4096 IDs per ms per worker)
 *
 * <p>
 * <p>
 * The final ID is structured as:
 * | 0 | 00000000000000000000000000000000000000000 | 00000 | 00000 | 000000000000 |
 * | - | ------------------------------------------- | ----- | ----- | ------------ |
 * | 1 | 41 bits (timestamp)                         | 5 bits| 5 bits| 12 bits      |
 * | s | (current_ms - epoch)                        | dc_id | w_id  | sequence     |
 */
public class SnowflakeUtil {

    // 自定义纪元时间（2025-01-01T00:00:00Z）。初始定义，不可更改。
    // 这个时间戳是计算ID时的时间戳偏移量的起点。
    private static final long EPOCH = 1735689600000L;

    // 工作节点ID的位数 (5 bits, 0-31)
    private static final long WORKER_ID_BITS = 5L;
    // 数据中心ID的位数 (5 bits, 0-31)
    private static final long DATACENTER_ID_BITS = 5L;

    // 支持的最大工作节点ID (31)
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    // 支持的最大数据中心ID (31)
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    // 序列号的位数 (12 bits, 0-4095)
    private static final long SEQUENCE_BITS = 12L;

    // 工作节点ID向左的位移 (12)
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    // 数据中心ID向左的位移 (17)
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    // 时间戳向左的位移 (22)
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    // 序列号的掩码 (4095)
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private final long workerId;
    private final long datacenterId;

    // 同一毫秒内的序列号 (0-4095)
    private long sequence = 0L;
    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     *
     * @param workerId     工作节点ID (0-31)
     * @param datacenterId 数据中心ID (0-31)
     */
    public SnowflakeUtil(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter ID can't be greater than %d or less than 0", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 线程安全地获取下一个ID。
     *
     * @return Snowflake ID
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 检测到时钟回拨
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果在同一毫秒内
        if (lastTimestamp == timestamp) {
            // 序列号递增，并用掩码确保它不会超过12位所能表示的最大值
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 如果序列号溢出（即达到4096），则等待下一毫秒
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 如果是新的毫秒，则序列号重置为0
            sequence = 0L;
        }

        // 更新最后的时间戳
        lastTimestamp = timestamp;

        // 通过位运算将各个部分组合成最终的64位ID
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT) |
                (datacenterId << DATACENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    /**
     * 阻塞直到下一个毫秒。
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回当前时间的毫秒数。
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
