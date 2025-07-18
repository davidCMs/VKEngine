package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidCMs.vkengine.util.VkUtils;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK14;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class VkSwapchainContext {

    private static final Logger log = LogManager.getLogger(VkSwapchainContext.class, VulkanMessageFactory.INSTANCE);
    private long swapchain = VK14.VK_NULL_HANDLE;
    private final VkSwapchainBuilder builder;

    private AtomicReference<List<VkImageView>> images = new AtomicReference<>();

    private Vector2i extent;

    VkSwapchainContext(VkSwapchainBuilder builder) {
        this.builder = builder;
        rebuild();
    }

    public void rebuild() {
        log.debug("Rebuilding swapchain");
        VkSwapchainBuilder builder = this.builder.copy();
        long oldSwapchain = swapchain;
        swapchain = builder.build(swapchain);

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

        if (err != VK14.VK_SUCCESS) {
            if (VkUtils.successful(err))
                log.warn("waring while trying to acquire next swapchain image: {}", VkUtils.translateErrorCode(err));
            else
                throw new RuntimeException("Failed to acquire next image from swapchain: " + VkUtils.translateErrorCode(err));
        }

        return i[0];
    }

    public VkImageView getImageView(int index) {
        return images.get().get(index);
    }

    private void replaceImageViews(List<VkImageView> newImageViews) {
        List<VkImageView> oldImageViews = images.getAndSet(newImageViews);
        if (oldImageViews == null)
            return;
        for (VkImageView imageContext : oldImageViews) {
            imageContext.destroy();
        }
    }

    public AtomicReference<List<VkImageView>> getImages() {
        return images;
    }

    public long getSwapchain() {
        return swapchain;
    }

    public VkSwapchainBuilder getBuilder() {
        return builder;
    }

    public Vector2i getExtent() {
        return extent;
    }

    public void destroy() {
        replaceImageViews(null);
        KHRSwapchain.vkDestroySwapchainKHR(builder.getDevice().device(), swapchain, null);
    }
}
