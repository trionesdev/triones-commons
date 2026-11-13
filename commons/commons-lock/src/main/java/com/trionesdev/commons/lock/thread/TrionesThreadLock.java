package com.trionesdev.commons.lock.thread;

import com.trionesdev.commons.lock.TrionesLock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * 进程内锁，同名共享同一把 {@link ThreadLock}。
 */
public class TrionesThreadLock implements TrionesLock {
    private static final ConcurrentMap<String, ThreadLock> LOCK_MAP = new ConcurrentHashMap<>();
    private final ThreadLock lock;

    public TrionesThreadLock(String lockName) {
        this.lock = LOCK_MAP.computeIfAbsent(lockName, k -> new ThreadLock());
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

    /**
     * 锁空闲（含租约已到期）时不做任何操作；被其他线程持有时抛出 {@link IllegalMonitorStateException}。
     */
    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }
}
