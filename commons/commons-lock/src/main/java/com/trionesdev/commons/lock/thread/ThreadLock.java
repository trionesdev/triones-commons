package com.trionesdev.commons.lock.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 进程内可重入锁，支持租约（leaseTime）到期自动释放。
 * <p>
 * {@link ReentrantLock} 只能由持有线程释放，无法实现"到期由其他线程释放"，
 * 因此这里自行维护持有者、重入次数和到期时间：租约到期后锁视为已释放，等待线程可直接获取，
 * 原持有线程随后调用 {@link #unlock()} 不做任何操作。
 * <p>
 * 时间参数约定与 Redisson 一致：{@code waitTime < 0} 表示一直等待，{@code leaseTime <= 0} 表示不过期。
 */
public class ThreadLock implements Lock {
    private static final long NO_LEASE = 0L;

    /** 保护以下状态的内部互斥锁，只在极短的临界区内持有。 */
    private final ReentrantLock sync = new ReentrantLock();
    private final Condition released = sync.newCondition();

    private Thread owner;
    private int holds;
    /** 到期时刻（System.nanoTime 基准）；未设置租约时为 NO_LEASE。 */
    private long expireAtNanos = NO_LEASE;

    // ------------------------------------------------------------------ Lock

    @Override
    public void lock() {
        boolean interrupted = false;
        while (true) {
            try {
                acquire(-1, NO_LEASE);
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        acquire(-1, NO_LEASE);
    }

    @Override
    public boolean tryLock() {
        sync.lock();
        try {
            return tryAcquire(NO_LEASE);
        } finally {
            sync.unlock();
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return acquire(Math.max(0, unit.toNanos(time)), NO_LEASE);
    }

    /**
     * @param waitTime  最长等待时间，小于 0 表示一直等待
     * @param leaseTime 持有时长，到期自动释放；小于等于 0 表示不过期
     */
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        long waitNanos = waitTime < 0 ? -1 : unit.toNanos(waitTime);
        long leaseNanos = leaseTime <= 0 ? NO_LEASE : unit.toNanos(leaseTime);
        return acquire(waitNanos, leaseNanos);
    }

    /**
     * 释放一次持有。锁已空闲（例如租约已到期）时不做任何操作；
     * 被其他线程持有时抛出 {@link IllegalMonitorStateException}。
     */
    @Override
    public void unlock() {
        sync.lock();
        try {
            expireIfNeeded();
            if (owner == null) {
                return;
            }
            if (owner != Thread.currentThread()) {
                throw new IllegalMonitorStateException("Lock is held by another thread: " + owner.getName());
            }
            if (--holds == 0) {
                owner = null;
                expireAtNanos = NO_LEASE;
                released.signalAll();
            }
        } finally {
            sync.unlock();
        }
    }

    /**
     * 不支持条件变量：租约到期会在持有线程不知情时释放锁，与 Condition 的语义冲突。
     */
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("ThreadLock does not support conditions");
    }

    // ----------------------------------------------------------------- state

    public boolean isLocked() {
        sync.lock();
        try {
            expireIfNeeded();
            return owner != null;
        } finally {
            sync.unlock();
        }
    }

    public boolean isHeldByCurrentThread() {
        sync.lock();
        try {
            expireIfNeeded();
            return owner == Thread.currentThread();
        } finally {
            sync.unlock();
        }
    }

    // --------------------------------------------------------------- helpers

    /**
     * @param waitNanos 小于 0 表示一直等待
     */
    private boolean acquire(long waitNanos, long leaseNanos) throws InterruptedException {
        sync.lockInterruptibly();
        try {
            long deadline = System.nanoTime() + waitNanos;
            while (true) {
                if (tryAcquire(leaseNanos)) {
                    return true;
                }
                long sleepNanos = Long.MAX_VALUE;
                if (waitNanos >= 0) {
                    sleepNanos = deadline - System.nanoTime();
                    if (sleepNanos <= 0) {
                        return false;
                    }
                }
                // 持有者带租约时，最晚在到期时刻醒来，无需持有者通知。
                if (expireAtNanos != NO_LEASE) {
                    sleepNanos = Math.min(sleepNanos, Math.max(1L, expireAtNanos - System.nanoTime()));
                }
                if (sleepNanos == Long.MAX_VALUE) {
                    released.await();
                } else {
                    released.awaitNanos(sleepNanos);
                }
            }
        } finally {
            sync.unlock();
        }
    }

    /** 调用方须持有 sync。 */
    private boolean tryAcquire(long leaseNanos) {
        expireIfNeeded();
        Thread current = Thread.currentThread();
        if (owner == null) {
            owner = current;
            holds = 1;
            expireAtNanos = leaseNanos == NO_LEASE ? NO_LEASE : expireAt(leaseNanos);
            return true;
        }
        if (owner == current) {
            holds++;
            // 与 Redisson 一致：重入时指定租约会刷新到期时间。
            if (leaseNanos != NO_LEASE) {
                expireAtNanos = expireAt(leaseNanos);
            }
            return true;
        }
        return false;
    }

    /** 调用方须持有 sync。 */
    private void expireIfNeeded() {
        if (owner != null && expireAtNanos != NO_LEASE && System.nanoTime() - expireAtNanos >= 0) {
            owner = null;
            holds = 0;
            expireAtNanos = NO_LEASE;
            released.signalAll();
        }
    }

    private static long expireAt(long leaseNanos) {
        long at = System.nanoTime() + leaseNanos;
        // 避开哨兵值。
        return at == NO_LEASE ? 1L : at;
    }
}
