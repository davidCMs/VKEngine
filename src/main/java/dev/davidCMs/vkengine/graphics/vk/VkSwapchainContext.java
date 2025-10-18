package dev.davidCMs.vkengine.graphics.vk;

import org.tinylog.TaggedLogger;
import dev.davidCMs.vkengine.util.VkUtils;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK14;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VkSwapchainContext {

    private static final TaggedLogger log = org.tinylog.Logger.tag("Vulkan");
    private long swapchain = VK14.VK_NULL_HANDLE;
    private final VkSwapchainBuilder builder;

    private final ReentrantReadWriteLock swapchainRebuildLock = new ReentrantReadWriteLock();

    private AtomicReference<List<VkImageView>> images = new AtomicReference<>();

    private Vector2i extent;

    VkSwapchainContext(VkSwapchainBuilder builder) {
        this.builder = builder;
        rebuild();
    }

    public void rebuild() {
        swapchainRebuildLock.writeLock().lock();

        try {
            log.debug("Rebuilding swapchain");
            builder.getDevice().waitIdle();

            VkSwapchainBuilder builder = this.builder.copy();

            extent = this.builder.getWindow().getFrameBufferSize();
            this.builder.setImageExtent(extent);
            builder.setImageExtent(extent);

            long oldSwapchain = swapchain;
            swapchain = builder.build(oldSwapchain);

            if (oldSwapchain != VK14.VK_NULL_HANDLE)
                KHRSwapchain.vkDestroySwapchainKHR(builder.getDevice().device(), oldSwapchain, null);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                int err;
                IntBuffer imageCount = stack.callocInt(1);
                err = KHRSwapchain.vkGetSwapchainImagesKHR(builder.getDevice().device(), swapchain, imageCount, null);
                if (err != VK14.VK_SUCCESS) throw new RuntimeException("Cannot get swapchain images count");

                int count = imageCount.get(0);

                LongBuffer images = stack.callocLong(count);
                err = KHRSwapchain.vkGetSwapchainImagesKHR(builder.getDevice().device(), swapchain, imageCount, images);
                if (err != VK14.VK_SUCCESS) throw new RuntimeException("Cannot get swapchain images");


                VkImageViewBuilder imageViewBuilder = new VkImageViewBuilder(builder.getDevice());
                imageViewBuilder.setImageFormat(builder.getImageFormat());
                imageViewBuilder.setImageViewType(VkImageType.TYPE_2D);
                imageViewBuilder.setImageSubresourceRange(new VkImageSubresourceRangeBuilder()
                        .setAspectMask(VkAspectMask.COLOR)
                        .setBaseLayer(0)
                        .setBaseMipLevel(0)
                        .setLayerCount(1)
                        .setLevelCount(1));

                List<VkImageView> newImageViews = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    VkImage img = new VkImage(
                            images.get(i),
                            builder.getDevice(),
                            VkImageType.TYPE_2D,
                            builder.getImageFormat(),
                            builder.getImageExtent().x,
                            builder.getImageExtent().y,
                            1,
                            1,
                            builder.getImageArrayLayers(),
                            VkSampleCount.SAMPLE_1,
                            VkImageTiling.OPTIMAL,
                            builder.getImageUsage(),
                            builder.getQueueFamilies().size() > 1 ? VkSharingMode.CONCURRENT : VkSharingMode.EXCLUSIVE
                    );
                    newImageViews.add(i, imageViewBuilder.build(img));

                }

                replaceImageViews(newImageViews);
                extent = builder.getImageExtent();
            }
        } finally {
            swapchainRebuildLock.writeLock().unlock();
        }

    }

    public int acquireNextImage(VkFence fence) {
        return acquireNextImage(-1, fence);
    }

    public int acquireNextImage(VkBinarySemaphore semaphore) {
        return acquireNextImage(-1, semaphore);
    }

    public int acquireNextImage(VkBinarySemaphore semaphore, VkFence fence) {
        return acquireNextImage(-1, semaphore, fence);
    }

    public int acquireNextImage(long timeout, VkFence fence) {
        return acquireNextImage(timeout, null, fence);
    }

    public int acquireNextImage(long timeout, VkBinarySemaphore semaphore) {
        return acquireNextImage(timeout, semaphore, null);
    }

    public int acquireNextImage(long timeout, VkBinarySemaphore semaphore, VkFence fence) {
        swapchainRebuildLock.readLock().lock();
        try {
            long semaphoreL = semaphore != null ? semaphore.getSemaphore() : 0;
            long fenceL = fence != null ? fence.getFence() : 0;

            int[] i = new int[1];

            int err;
            err = KHRSwapchain.vkAcquireNextImageKHR(
                    builder.getDevice().device(),
                    swapchain,
                    timeout,
                    semaphoreL,
                    fenceL,
                    i
            );

            if (!extent.equals(builder.getWindow().getFrameBufferSize()) || //FUCK WAYLAND
                err == KHRSwapchain.VK_SUBOPTIMAL_KHR || err == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
                log.info("Window properties changed, the swapchain needs to be rebuilt");
                return -1;
            }

            if (err != VK14.VK_SUCCESS) {
                if (VkUtils.successful(err)) {
                    log.warn("warning while trying to acquire next swapchain image: {}", VkUtils.translateErrorCode(err));
                } else
                    throw new RuntimeException("Failed to acquire next image from swapchain: " + VkUtils.translateErrorCode(err));
            }

            return i[0];
        } finally {
            swapchainRebuildLock.readLock().unlock();
        }
    }

    public VkImageView getImageView(int index) {
        swapchainRebuildLock.readLock().lock();
        try {
            return images.get().get(index);
        } finally {
            swapchainRebuildLock.readLock().unlock();
        }
    }

    public int getImageCount() {
        swapchainRebuildLock.readLock().lock();
        try {
            return images.get().size();
        } finally {
            swapchainRebuildLock.readLock().unlock();
        }
    }

    private void replaceImageViews(List<VkImageView> newImageViews) {
        List<VkImageView> oldImageViews = images.getAndSet(newImageViews);
        if (oldImageViews == null)
            return;
        for (VkImageView imageContext : oldImageViews) {
            imageContext.destroy();
        }
    }

    public long getSwapchain() {
        swapchainRebuildLock.readLock().lock();
        try {
            return swapchain;
        } finally {
            swapchainRebuildLock.readLock().unlock();
        }
    }

    public VkSwapchainBuilder getBuilder() {
        return builder;
    }

    public Vector2i getExtent() {
        swapchainRebuildLock.readLock().lock();
        try {
            return extent;
        } finally {
            swapchainRebuildLock.readLock().unlock();
        }
    }

    public void destroy() {
        replaceImageViews(null);
        KHRSwapchain.vkDestroySwapchainKHR(builder.getDevice().device(), swapchain, null);
    }
}
