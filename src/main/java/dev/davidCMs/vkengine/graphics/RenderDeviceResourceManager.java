package dev.davidCMs.vkengine.graphics;

import dev.davidCMs.vkengine.graphics.vk.VkDeviceContext;
import dev.davidCMs.vkengine.graphics.vk.VkFence;
import dev.davidCMs.vkengine.graphics.vk.VkQueue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RenderDeviceResourceManager {

    public static class TransferManager {
        private final VkDeviceContext device;
        private final VkQueue queue;
        private final ConcurrentHashMap<VkFence, Runnable> callbacks = new ConcurrentHashMap<>();
        private final ExecutorService executor;
        private final Thread callBackThread;
        private final Object lock = new Object();

        private Thread createCallbackThread() {
            return new Thread(() -> {
                while (!Thread.interrupted()) {
                    synchronized (lock) {
                        while (callbacks.isEmpty()) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                    for (Map.Entry<VkFence, Runnable> entry : callbacks.entrySet()) {
                        if (entry.getKey().isSignaled()) {
                            executor.submit(entry.getValue());
                            callbacks.remove(entry.getKey(), entry.getValue());
                        }
                    }
                }
            }, "TransferQueue " + queue.getQueueFamily().getIndex() + " Callback");
        }

        public TransferManager(VkQueue queue, ExecutorService executor, VkDeviceContext device) {
            this.queue = queue;
            this.executor = executor;
            this.device = device;
            this.callBackThread = createCallbackThread();

            this.callBackThread.start();
        }

        public void submit(VkFence fence, Runnable runnable, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
            synchronized (lock) {
                callbacks.put(fence, runnable);
                queue.submit(fence, submitInfoBuilder);
                lock.notifyAll();
            }
        }

        public void submit(VkFence fence, Runnable runnable, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
            synchronized (lock) {
                callbacks.put(fence, runnable);
                queue.submit(fence, submitInfoBuilder);
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

        public void submit(VkFence fence, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
            queue.submit(fence, submitInfoBuilder);
        }

        public void submit(VkFence fence, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
            queue.submit(fence, submitInfoBuilder);
        }

        public void submit(VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
            queue.submit(submitInfoBuilder);
        }

        public void submit(Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
            queue.submit(submitInfoBuilder);
        }

        public void stop() {
            callBackThread.interrupt();
            try {
                callBackThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final List<TransferManager> transferManagers;
    private final ExecutorService transferManagerCallBackExecutor;

    public RenderDeviceResourceManager(VkDeviceContext device, Collection<VkQueue> transferManagers) {
        for (VkQueue queue : transferManagers)
            if (!queue.getQueueFamily().capableOfTransfer())
                throw new IllegalArgumentException("One or more queues do not support transfer ");

        this.transferManagerCallBackExecutor = Executors.newVirtualThreadPerTaskExecutor();

        List<TransferManager> managers = new ArrayList<>();
        for (VkQueue queue : transferManagers) {
            managers.add(new TransferManager(queue, transferManagerCallBackExecutor, device));
        }
        this.transferManagers = Collections.unmodifiableList(managers);
    }

    public void destroy() {
        for (TransferManager manager : transferManagers)
            manager.stop();
        transferManagerCallBackExecutor.shutdownNow();
    }

    public void submit(VkFence fence, Runnable runnable, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
        transferManagers.getFirst().submit(fence, runnable, submitInfoBuilder);
    }

    public void submit(VkFence fence, Runnable runnable, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
        transferManagers.getFirst().submit(fence, runnable, submitInfoBuilder);
    }

    public void submit(Runnable runnable, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
        transferManagers.getFirst().submit(runnable, submitInfoBuilder);
    }

    public void submit(Runnable runnable, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
        transferManagers.getFirst().submit(runnable, submitInfoBuilder);
    }

    public void submit(VkFence fence, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
        transferManagers.getFirst().submit(fence, submitInfoBuilder);
    }

    public void submit(VkFence fence, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
        transferManagers.getFirst().submit(fence, submitInfoBuilder);
    }

    public void submit(VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
        transferManagers.getFirst().submit(submitInfoBuilder);
    }

    public void submit(Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
        transferManagers.getFirst().submit(submitInfoBuilder);
    }

}
