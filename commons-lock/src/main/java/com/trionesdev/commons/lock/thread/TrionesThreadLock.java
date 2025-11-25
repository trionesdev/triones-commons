package com.trionesdev.commons.lock.thread;

import com.trionesdev.commons.lock.TrionesLock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class TrionesThreadLock implements TrionesLock {
    private static final Map<String, ThreadLock> LOCK_MAP = new HashMap<String, ThreadLock>();
    private final ThreadLock lock;

    public TrionesThreadLock(String lockName) {
        lock = getLock(lockName);
    }

    @Override
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        return lock.tryLock(waitTime, leaseTime, unit);
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
        if (lock.isLocked()){
            lock.unlock();
        }
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }

    private ThreadLock getLock(String lockName) {
        ThreadLock lock;
        synchronized (LOCK_MAP) {
             lock = LOCK_MAP.computeIfAbsent(lockName, k -> new ThreadLock());
        }
        return lock;
    }
}
