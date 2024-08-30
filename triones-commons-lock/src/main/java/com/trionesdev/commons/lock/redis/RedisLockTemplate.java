package com.trionesdev.commons.lock.redis;

import com.trionesdev.commons.lock.TrionesLock;
import com.trionesdev.commons.lock.TrionesLockTemplate;
import org.redisson.api.RedissonClient;

public class RedisLockTemplate implements TrionesLockTemplate {
    private final RedissonClient redissonClient;

    public RedisLockTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public TrionesLock getLock(String lockName) {
        return new TrionesRedisLock(redissonClient.getLock(lockName));
    }

}
