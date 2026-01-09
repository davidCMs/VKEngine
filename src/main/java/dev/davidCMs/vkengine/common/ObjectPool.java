package dev.davidCMs.vkengine.common;

import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ObjectPool<Type extends Poolable> implements Destroyable {

    public enum State {
        ALIVE,
        DESTROYING,
        DESTROYED
    }

    public class Lease implements Destroyable, AutoCloseable {
        private final ObjectPool<Type> pool;
        private final Type object;

        public Lease(ObjectPool<Type> pool, Type object) {
            this.pool = pool;
            this.object = object;
        }

        public Type get() {
            return object;
        }

        public void release() {
            destroy();
        }

        @Override
        public void close() {
            destroy();
        }

        @Override
        public void destroy() {
            lock.lock();
            try {
                if (pool.getState() == State.ALIVE && pool.recycle) {
                    object.reset();
                    pool.pool.add(this);

                    return;
                }
            } finally {
                lock.unlock();
            }
            if (object instanceof Destroyable destroyable) {
                destroyable.destroy();
            }
        }
    }

    private final boolean recycle;
    private final ConcurrentLinkedQueue<Lease> pool = new ConcurrentLinkedQueue<>();
    private final Supplier<Type> factory;
    private final ReentrantLock lock = new ReentrantLock();

    private State state = State.ALIVE;

    public ObjectPool(Supplier<Type> factory, int initialSize, boolean recycle) {
        this.factory = factory;
        this.recycle = recycle;

        if (!recycle) {
            for (int i = 0; i < initialSize; i++) {
                pool.add(new Lease(this, factory.get()));
            }
        }
    }

    public ObjectPool(Supplier<Type> factory) {
        this(factory, 0, true);
    }

    public ObjectPool(Supplier<Type> factory, int initialSize) {
        this(factory, initialSize, true);
    }

    private Lease newLease() {
        return new Lease(this, factory.get());
    }

    public Lease get() {
        Lease lease;

        lock.lock();
        try {
            if (state != State.ALIVE) throw new RuntimeException("Cannot get objects when the pool is destroying");
            if (!recycle)
                return newLease();
            lease = pool.poll();
        } finally {
            lock.unlock();
        }

        if (lease == null) {
            return newLease();
        }
        return lease;
    }

    public boolean isDestroyed() {
        lock.lock();
        try {
            return state == State.DESTROYED;
        } finally {
            lock.unlock();
        }
    }

    public State getState() {
        lock.lock();
        try {
            return state;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void destroy() {
        lock.lock();
        try {
            state = State.DESTROYING;
        } finally {
            lock.unlock();
        }

        Lease instance = pool.poll();
        if (instance != null && instance.object instanceof Destroyable destroyable) {
            do destroyable.destroy();
            while ((destroyable = pool.poll()) != null);
        }

        lock.lock();
        try {
            state = State.DESTROYED;
        } finally {
            lock.unlock();
        }
    }
}
