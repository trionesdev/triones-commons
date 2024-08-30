package com.trionesdev.commons.lock;

import com.trionesdev.commons.lock.thread.TrionesThreadLock;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class LockTest {

    public void threadLockRun() {
        TrionesThreadLock threadLock = new TrionesThreadLock("threadLock");
        try {
            boolean locked = threadLock.tryLock(10000, 3000, TimeUnit.MILLISECONDS);
            if (locked){
                System.out.println("threadLock" + LocalDateTime.now().toString());
                Thread.sleep(5000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            threadLock.unlock();
        }
    }

    @Test
    public void threadLock() throws InterruptedException {

        Thread thread1 = new Thread(() -> threadLockRun());
        thread1.start();
        System.out.println("thread1 start");
//        Thread.sleep(2000);
        Thread thread2 = new Thread(() -> threadLockRun());
        thread2.start();
        System.out.println("thread2 start");
        Thread.sleep(100000);
    }

}
