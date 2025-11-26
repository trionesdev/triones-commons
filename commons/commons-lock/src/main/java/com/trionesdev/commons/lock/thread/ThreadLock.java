package com.trionesdev.commons.lock.thread;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadLock extends ReentrantLock {
    
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        long leaseTimeInMillis = unit.toMillis(leaseTime);
        Instant expireAt = Instant.now().plusMillis(leaseTimeInMillis);
        boolean res = super.tryLock(waitTime, unit);
        CompletableFuture.runAsync(() -> {
            while (res && super.isLocked() && expireAt.isBefore(Instant.now())) {
                super.unlock();
            }
        });
        return res;
    }

}
