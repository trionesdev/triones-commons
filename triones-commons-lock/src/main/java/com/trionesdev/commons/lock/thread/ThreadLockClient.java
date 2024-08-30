package com.trionesdev.commons.lock.thread;

import com.trionesdev.commons.lock.TrionesLock;
import com.trionesdev.commons.lock.TrionesLockTemplate;

public class ThreadLockClient implements TrionesLockTemplate {
    public TrionesLock getLock(String lockName) {
        return new TrionesThreadLock(lockName);
    }
}
