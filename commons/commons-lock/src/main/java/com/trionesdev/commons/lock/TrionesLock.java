package com.trionesdev.commons.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public interface TrionesLock extends Lock {
    boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;
}
