package com.trionesdev.commons.lock.redis;

import com.trionesdev.commons.lock.TrionesLock;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class TrionesRedisLock implements TrionesLock {

    private final RLock lock;

    public TrionesRedisLock(RLock lock) {
        this.lock = lock;
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }

    @Override
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        return lock.tryLock(waitTime, leaseTime, unit);
    }
}
