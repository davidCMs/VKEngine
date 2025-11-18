package dev.davidCMs.vkengine.graphics;

import dev.davidCMs.vkengine.common.Destroyable;
import dev.davidCMs.vkengine.common.Fence;
import dev.davidCMs.vkengine.common.IFence;
import dev.davidCMs.vkengine.common.ISignalableFence;
import dev.davidCMs.vkengine.graphics.vk.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

public class RenderDeviceResourceManager implements Destroyable {

    public static class TransferManager {
        private final VkDeviceContext device;
        private final VkQueue queue;
        private final ConcurrentHashMap<VkFence, Runnable> callbacks = new ConcurrentHashMap<>();
        private final ExecutorService executor;
        private final Thread callBackThread;
        private final Object lock = new Object();
        private final ThreadLocal<VkCommandPool> pool;
        private final List<VkCommandPool> pools;

        private Thread createCallbackThread() {
            return new Thread(() -> {
                while (!Thread.interrupted()) {
                    synchronized (lock) {
                        while (callbacks.isEmpty()) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }
                    for (Map.Entry<VkFence, Runnable> entry : callbacks.entrySet()) {
                        if (entry.getKey().isSignaled()) {
                            Runnable r = entry.getValue();
                            callbacks.remove(entry.getKey(), entry.getValue());
                            executor.submit(r);
                        }
                    }
                }
            }, "TransferQueue " + queue.getQueueFamily().getIndex() + " Callback");
        }

        public TransferManager(VkQueue queue, ExecutorService executor, VkDeviceContext device) {
            this.queue = queue;
            this.executor = executor;
            this.device = device;
            this.pools = new ArrayList<>();
            this.pool = ThreadLocal.withInitial(() -> {
                VkCommandPool pool = queue.getQueueFamily().createCommandPool(device, VkCommandPoolCreateFlags.TRANSIENT);
                pools.add(pool);
                return pool;
            });
            this.callBackThread = createCallbackThread();

            this.callBackThread.start();
        }

        public void submit(IFence fence, Runnable runnable, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
            synchronized (lock) {
                switch (fence) {
                    case VkFence realFence -> {
                        callbacks.put(realFence, () -> {
                            realFence.destroy();
                            runnable.run();
                        });
                        queue.submit(realFence, submitInfoBuilder);
                    }

                    case ISignalableFence fakeFence -> {
                        VkFence realFence = new VkFence(device);
                        callbacks.put(realFence, () -> {
                            realFence.destroy();
                            runnable.run();
                            fakeFence.signal();
                        });
                        queue.submit(realFence, submitInfoBuilder);
                    }
                    default -> throw new IllegalArgumentException("Unsupported class of IFence " + fence.getClass());
                }

                lock.notifyAll();
            }
        }

        public void submit(IFence fence, Runnable runnable, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
            synchronized (lock) {
                switch (fence) {
                    case VkFence realFence -> {
                        callbacks.put(realFence, () -> {
                            realFence.destroy();
                            runnable.run();
                        });
                        queue.submit(realFence, submitInfoBuilder);
                    }

                    case ISignalableFence fakeFence -> {
                        VkFence realFence = new VkFence(device);
                        callbacks.put(realFence, () -> {
                            realFence.destroy();
                            runnable.run();
                            fakeFence.signal();
                        });
                        queue.submit(realFence, submitInfoBuilder);
                    }
                    default -> throw new IllegalArgumentException("Unsupported class of IFence " + fence.getClass());
                }

                lock.notifyAll();
            }
        }

        public void submit(IFence fence, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
            synchronized (lock) {
                switch (fence) {
                    case VkFence realFence -> {
                        callbacks.put(realFence, realFence::destroy);
                        queue.submit(realFence, submitInfoBuilder);
                    }

                    case ISignalableFence fakeFence -> {
                        VkFence realFence = new VkFence(device);
                        callbacks.put(realFence, () -> {
                            realFence.destroy();
                            fakeFence.signal();
                        });
                        queue.submit(realFence, submitInfoBuilder);
                    }
                    default -> throw new IllegalArgumentException("Unsupported class of IFence " + fence.getClass());
                }

                lock.notifyAll();
            }
        }

        public void submit(IFence fence, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
            synchronized (lock) {
                switch (fence) {
                    case VkFence realFence -> {
                        callbacks.put(realFence, realFence::destroy);
                        queue.submit(realFence, submitInfoBuilder);
                    }

                    case ISignalableFence fakeFence -> {
                        VkFence realFence = new VkFence(device);
                        callbacks.put(realFence, () -> {
                            realFence.destroy();
                            fakeFence.signal();
                        });
                        queue.submit(realFence, submitInfoBuilder);
                    }
                    default -> throw new IllegalArgumentException("Unsupported class of IFence " + fence.getClass());
                }

                lock.notifyAll();
            }
        }

        public void submit(Runnable runnable, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
            synchronized (lock) {
                VkFence fence = new VkFence(device);
                callbacks.put(fence, () -> {
                    fence.destroy();
                    runnable.run();
                });
                queue.submit(fence, submitInfoBuilder);
                lock.notifyAll();
            }
        }

        public void submit(Runnable runnable, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
            synchronized (lock) {
                VkFence fence = new VkFence(device);
                callbacks.put(fence, () -> {
                    fence.destroy();
                    runnable.run();
                });
                queue.submit(fence, submitInfoBuilder);
                lock.notifyAll();
            }
        }

        public void stop() {
            callBackThread.interrupt();
            try {
                callBackThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (VkCommandPool pool : pools) {
                pool.destroy();
            }
        }
    }

    private final List<TransferManager> transferManagers;
    private final ExecutorService transferManagerCallBackExecutor;

    public RenderDeviceResourceManager(VkDeviceContext device, Collection<VkQueue> transferManagers) {
        for (VkQueue queue : transferManagers)
            if (!queue.getQueueFamily().capableOfTransfer())
                throw new IllegalArgumentException("One or more queues do not support transfer ");

        this.transferManagerCallBackExecutor = Executors.newCachedThreadPool();

        List<TransferManager> managers = new ArrayList<>();
        for (VkQueue queue : transferManagers) {
            managers.add(new TransferManager(queue, transferManagerCallBackExecutor, device));
        }
        this.transferManagers = Collections.unmodifiableList(managers);
    }

    @Override
    public void destroy() {
        transferManagerCallBackExecutor.shutdownNow();
        for (TransferManager manager : transferManagers)
            manager.stop();
    }

    private final AtomicInteger nextManager = new AtomicInteger(0);

    private TransferManager selectManager() {
        int i = nextManager.getAndUpdate((oldInt) -> {
           oldInt++;
           if (oldInt > transferManagers.size()-1) return 0;
           return oldInt;
        });
        return transferManagers.get(i);
    }

    public void submit(IFence fence, Runnable runnable, RenderDeviceSubmit submit) {
        TransferManager manager = selectManager();
        manager.submit(fence, runnable, submit.submit(manager.pool));
    }

    public void submit(Runnable runnable, RenderDeviceSubmit submit) {
        TransferManager manager = selectManager();
        manager.submit(runnable, submit.submit(manager.pool));
    }

    public void submit(IFence fence, RenderDeviceSubmit submit) {
        TransferManager manager = selectManager();
        manager.submit(fence, submit.submit(manager.pool));
    }

    public void submit(IFence fence, Runnable runnable, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
        selectManager().submit(fence, runnable, submitInfoBuilder);
    }

    public void submit(IFence fence, Runnable runnable, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
        selectManager().submit(fence, runnable, submitInfoBuilder);
    }

    public void submit(Runnable runnable, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
        selectManager().submit(runnable, submitInfoBuilder);
    }

    public void submit(Runnable runnable, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
        selectManager().submit(runnable, submitInfoBuilder);
    }

    public void submit(IFence fence, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
        selectManager().submit(fence, submitInfoBuilder);
    }

    public void submit(IFence fence, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
        selectManager().submit(fence, submitInfoBuilder);
    }

}
