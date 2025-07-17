package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK14;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

public class VkSwapchainContext {

    private static final Logger log = LogManager.getLogger(VkSwapchainContext.class, VulkanMessageFactory.INSTANCE);
    private long swapchain = VK14.VK_NULL_HANDLE;
    private final VkSwapchainBuilder builder;

    private volatile List<VkImageView> images = new ArrayList<>();
    private int imageCount;

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
            LongBuffer images = stack.callocLong(imageCount.get(0));
            err = KHRSwapchain.vkGetSwapchainImagesKHR(builder.getDevice().device(), swapchain, imageCount, images);
            if (err != VK14.VK_SUCCESS) throw new RuntimeException("Cannot get swapchain images");

            this.imageCount = imageCount.get(0);

            VkImageViewBuilder imageViewBuilder = new VkImageViewBuilder(builder.getDevice());
            imageViewBuilder.setImageFormat(builder.getImageFormat());
            imageViewBuilder.setImageViewType(VkImageType.TYPE_2D);
            imageViewBuilder.setImageSubresourceRange(new VkImageSubresourceRangeBuilder()
                    .setAspectMask(VkAspectMask.COLOR)
                    .setBaseLayer(0)
                    .setBaseMipLevel(0)
                    .setLayerCount(1)
                    .setLevelCount(1));

            List<VkImageView> newImageViews = new ArrayList<>(this.imageCount);
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
                        builder.getImageUsage(),
                        builder.getQueueFamilies().size() > 1 ? VkSharingMode.CONCURRENT : VkSharingMode.EXCLUSIVE
                );
                newImageViews.add(i, imageViewBuilder.build(img));

            }

            replaceImageViews(newImageViews);
            extent = builder.getImageExtent();
        }

    }

    private void replaceImageViews(List<VkImageView> newImageViews) {
        List<VkImageView> oldImageViews = this.images;
        this.images = newImageViews;
        for (VkImageView imageContext : oldImageViews) {
            imageContext.destroy();
        }
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
