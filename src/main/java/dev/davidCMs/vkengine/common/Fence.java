package dev.davidCMs.vkengine.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Fence implements ISignalableFence {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition signaled = lock.newCondition();
    private volatile boolean isSignaled = false;
    private volatile boolean destroyed = false;

    public Fence(boolean signaled) {
        this.isSignaled = signaled;
    }

    public Fence() {
        this(false);
    }

    @Override
    public Fence waitFor(long timeout) {
        lock.lock();
        try {
            if (timeout < 0) {
                while (!isSignaled && !destroyed) {
                    signaled.await();
                }
            } else {
                while (!isSignaled && !destroyed) {
                    if (!signaled.await(timeout, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return this;
    }

    @Override
    public boolean isSignaled() {
        lock.lock();
        try {
            return destroyed || isSignaled;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Fence reset() {
        lock.lock();
        try {
            isSignaled = false;
            signaled.signalAll();
        } finally {
            lock.unlock();
        }
        return this;
    }

    public Fence signal() {
        lock.lock();
        try {
            isSignaled = true;
            signaled.signalAll();
        } finally {
            lock.unlock();
        }
        return this;
    }

    @Override
    public void destroy() {
        lock.lock();
        try {
            destroyed = true;
            signaled.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

