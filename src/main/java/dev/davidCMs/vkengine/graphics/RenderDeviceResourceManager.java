package dev.davidCMs.vkengine.graphics;

import dev.davidCMs.vkengine.common.Destroyable;
import dev.davidCMs.vkengine.common.IFence;
import dev.davidCMs.vkengine.graphics.vk.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                VkFence fenceReal = new VkFence(device);
                callbacks.put(fenceReal, () -> {
                    fenceReal.destroy();
                    runnable.run();
                    fence.destroy();
                });
                queue.submit(fenceReal, submitInfoBuilder);
                lock.notifyAll();
            }
        }

        public void submit(IFence fence, Runnable runnable, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
            synchronized (lock) {
                VkFence fenceReal = new VkFence(device);
                callbacks.put(fenceReal, () -> {
                    fenceReal.destroy();
                    runnable.run();
                    fence.destroy();
                });
                queue.submit(fenceReal, submitInfoBuilder);
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

        public void submit(IFence fence, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
            synchronized (lock) {
                VkFence fenceReal = new VkFence(device);
                callbacks.put(fenceReal, () -> {
                    fenceReal.destroy();
                    fence.destroy();
                });
                queue.submit(fenceReal, submitInfoBuilder);
                lock.notifyAll();
            }
        }

        public void submit(IFence fence, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
            synchronized (lock) {
                VkFence fenceReal = new VkFence(device);
                callbacks.put(fenceReal, () -> {
                    fenceReal.destroy();
                    fence.destroy();
                });
                queue.submit(fenceReal, submitInfoBuilder);
                lock.notifyAll();
            }
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

        this.transferManagerCallBackExecutor = Executors.newFixedThreadPool(2);

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

    private TransferManager selectManager() {
        return transferManagers.getFirst();
    }

    public void submit(IFence fence, Runnable runnable, RenderDeviceSubmit submit) {
        TransferManager manager = selectManager();
        manager.submit(fence, runnable, submit.submit(manager.pool));
    }

    public void submit(Runnable runnable, RenderDeviceSubmit submit) {
        TransferManager manager = selectManager();
        manager.submit(runnable, submit.submit(manager.pool));
    }

    public void submit(VkFence fence, RenderDeviceSubmit submit) {
        TransferManager manager = selectManager();
        manager.submit(fence, submit.submit(manager.pool));
    }

    public void submit(RenderDeviceSubmit submit) {
        TransferManager manager = selectManager();
        manager.submit(submit.submit(manager.pool));
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

    public void submit(VkFence fence, VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
        selectManager().submit(fence, submitInfoBuilder);
    }

    public void submit(VkFence fence, Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
        selectManager().submit(fence, submitInfoBuilder);
    }

    public void submit(VkQueue.VkSubmitInfoBuilder... submitInfoBuilder) {
        selectManager().submit(submitInfoBuilder);
    }

    public void submit(Collection<VkQueue.VkSubmitInfoBuilder> submitInfoBuilder) {
        selectManager().submit(submitInfoBuilder);
    }

}
