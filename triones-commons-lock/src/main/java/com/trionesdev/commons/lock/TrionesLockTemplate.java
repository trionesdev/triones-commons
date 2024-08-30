package com.trionesdev.commons.lock;

public interface TrionesLockTemplate {
    TrionesLock getLock(String lockName);
}
