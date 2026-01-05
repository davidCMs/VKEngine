package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.Destroyable;
import dev.davidCMs.vkengine.util.VkUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRExternalSemaphoreWin32;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK14;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

public class VkSwapchain implements Destroyable {

    private final long swapchain;
    private final VkImageView[] imageViews;
    private final int imageCount;
    private final VkDeviceContext device;

    VkSwapchain(VkSwapchainBuilder builder) {
        this(builder, null);
    }

    VkSwapchain(VkSwapchainBuilder builder, @Nullable VkSwapchain oldSwapchain) {
        this.swapchain = builder.build(oldSwapchain == null ? 0 : oldSwapchain.swapchain);
        this.device = builder.getDevice();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int err;
            IntBuffer imageCount = stack.callocInt(1);
            err = KHRSwapchain.vkGetSwapchainImagesKHR(builder.getDevice().device(), swapchain, imageCount, null);
            if (err != VK14.VK_SUCCESS) throw new RuntimeException("Cannot get swapchain images count");

            this.imageCount = imageCount.get(0);

            LongBuffer images = stack.callocLong(this.imageCount);
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

            VkImageView[] newImageViews = new VkImageView[this.imageCount];
            for (int i = 0; i < this.imageCount; i++) {
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
                        builder.imageUsage().getSet(),
                        builder.queueFamilies().size() > 1 ? VkSharingMode.CONCURRENT : VkSharingMode.EXCLUSIVE
                );
                newImageViews[i] = imageViewBuilder.build(img);

            }

            this.imageViews = newImageViews;
        }
    }

    public int acquireNextImage(VkFence fence) throws SwapchainSuboptimalException, SwapchainOutOfDateException {
        return acquireNextImage(-1, fence);
    }

    public int acquireNextImage(VkBinarySemaphore semaphore) throws SwapchainSuboptimalException, SwapchainOutOfDateException {
        return acquireNextImage(-1, semaphore);
    }

    public int acquireNextImage(VkBinarySemaphore semaphore, VkFence fence) throws SwapchainSuboptimalException, SwapchainOutOfDateException {
        return acquireNextImage(-1, semaphore, fence);
    }

    public int acquireNextImage(long timeout, VkFence fence) throws SwapchainSuboptimalException, SwapchainOutOfDateException {
        return acquireNextImage(timeout, null, fence);
    }

    public int acquireNextImage(long timeout, VkBinarySemaphore semaphore) throws SwapchainSuboptimalException, SwapchainOutOfDateException {
        return acquireNextImage(timeout, semaphore, null);
    }

    public static class SwapchainSuboptimalException extends Exception {
        public SwapchainSuboptimalException(String message) {
            super(message);
        }
    }

    public static class SwapchainOutOfDateException extends Exception {
        public SwapchainOutOfDateException(String message) {
            super(message);
        }
    }

    public int acquireNextImage(long timeout, VkBinarySemaphore semaphore, VkFence fence) throws
            SwapchainSuboptimalException, SwapchainOutOfDateException {
        long semaphoreL = semaphore != null ? semaphore.getSemaphore() : 0;
        long fenceL = fence != null ? fence.getFence() : 0;

        int[] i = new int[1];

        int err;
        err = KHRSwapchain.vkAcquireNextImageKHR(
                device.device(),
                swapchain,
                timeout,
                semaphoreL,
                fenceL,
                i
        );

        if (err == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
            throw new SwapchainOutOfDateException("Swpachain is out of date");
        }

        if (err == KHRSwapchain.VK_SUBOPTIMAL_KHR) {
            throw new SwapchainSuboptimalException("Swapchain is suboptimal");
        }

        if (err != VK14.VK_SUCCESS) {
            throw new RuntimeException("Failed to acquire next image from swapchain: " + VkUtils.translateErrorCode(err));
        }

        return i[0];

    }

    public VkImageView getImageView(int index) {
        return imageViews[index];
    }

    public int getImageCount() {
        return imageCount;
    }

    long getSwapchain() {
        return swapchain;
    }

    @Override
    public void destroy() {
        Destroyable.destroy(imageViews);
        KHRSwapchain.vkDestroySwapchainKHR(device.device(), swapchain, null);
    }
}
