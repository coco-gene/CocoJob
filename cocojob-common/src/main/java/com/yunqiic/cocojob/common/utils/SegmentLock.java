package com.yunqiic.cocojob.common.utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 分段锁
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
public class SegmentLock {

    private final int mask;
    private final Lock[] locks;

    public SegmentLock(int concurrency) {
        int size = CommonUtils.formatSize(concurrency);
        mask = size - 1;
        locks = new Lock[size];
        for (int i = 0; i < size; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    /**
     * 阻塞获取锁，可被打断
     * @param lockId 锁ID
     * @throws InterruptedException 线程被中断异常
     */
    public void lockInterruptible(int lockId) throws InterruptedException {
        Lock lock = locks[lockId & mask];
        lock.lockInterruptibly();
    }

    public void lockInterruptibleSafe(int lockId) {
        try {
            lockInterruptible(lockId);
        }catch (InterruptedException ignore) {
        }
    }

    /**
     * 释放锁
     * @param lockId 锁ID
     */
    public void unlock(int lockId) {
        Lock lock = locks[lockId & mask];
        lock.unlock();
    }
}
