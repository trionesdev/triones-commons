package com.trionesdev.commons.lock;

import com.trionesdev.commons.lock.thread.ThreadLock;
import com.trionesdev.commons.lock.thread.TrionesThreadLock;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LockTest {

    private static TrionesLock newLock() {
        return new TrionesThreadLock("lock-" + UUID.randomUUID());
    }

    @Test
    void leaseExpiresAndLetsOtherThreadAcquire() throws Exception {
        TrionesLock lock = newLock();
        assertTrue(lock.tryLock(0, 200, TimeUnit.MILLISECONDS));

        long start = System.nanoTime();
        boolean acquired = CompletableFuture.supplyAsync(() -> {
            try {
                boolean ok = lock.tryLock(2, -1, TimeUnit.SECONDS);
                if (ok) lock.unlock();
                return ok;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).get(5, TimeUnit.SECONDS);
        long waitedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        assertTrue(acquired, "lease should expire and release the lock");
        assertTrue(waitedMillis >= 150 && waitedMillis < 1500, "waited " + waitedMillis + "ms");
        // 原持有者在租约到期后释放：静默忽略。
        assertDoesNotThrow(lock::unlock);
    }

    @Test
    void withoutLeaseLockIsHeldUntilUnlock() throws Exception {
        TrionesLock lock = newLock();
        lock.lock();
        try {
            boolean acquired = CompletableFuture.supplyAsync(() -> {
                try {
                    return lock.tryLock(300, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).get(5, TimeUnit.SECONDS);
            assertFalse(acquired);
        } finally {
            lock.unlock();
        }
    }

    @Test
    void reentrantAndReleasedAfterMatchingUnlocks() throws Exception {
        ThreadLock lock = new ThreadLock();
        lock.lock();
        lock.lock();
        lock.unlock();
        assertTrue(lock.isHeldByCurrentThread());
        lock.unlock();
        assertFalse(lock.isLocked());
    }

    @Test
    void unlockByNonOwnerThrows() throws Exception {
        TrionesLock lock = newLock();
        lock.lock();
        try {
            Throwable error = CompletableFuture.runAsync(lock::unlock)
                    .handle((v, e) -> e).get(5, TimeUnit.SECONDS);
            assertInstanceOf(IllegalMonitorStateException.class, error.getCause());
        } finally {
            lock.unlock();
        }
    }

    @Test
    void sameNameSharesLock() throws Exception {
        String name = "shared-" + UUID.randomUUID();
        TrionesLock first = new TrionesThreadLock(name);
        first.lock();
        CountDownLatch done = new CountDownLatch(1);
        boolean[] acquired = new boolean[1];
        Thread other = new Thread(() -> {
            acquired[0] = new TrionesThreadLock(name).tryLock();
            done.countDown();
        });
        other.start();
        assertTrue(done.await(5, TimeUnit.SECONDS));
        assertFalse(acquired[0]);
        first.unlock();
    }

    @Test
    void zeroWaitFailsFastWhenHeld() throws Exception {
        TrionesLock lock = newLock();
        lock.lock();
        try {
            boolean acquired = CompletableFuture.supplyAsync(() -> {
                try {
                    return lock.tryLock(0, 1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).get(5, TimeUnit.SECONDS);
            assertFalse(acquired);
        } finally {
            lock.unlock();
        }
    }
}
