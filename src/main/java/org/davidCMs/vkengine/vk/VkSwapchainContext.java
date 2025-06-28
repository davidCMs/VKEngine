package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private VkImageViewBuilder imageViewBuilder;
    private volatile List<VkImageContext> image = new ArrayList<>();
    private int imageCount;

    public VkSwapchainContext(VkSwapchainBuilder builder) {
        this.builder = builder;
        this.imageViewBuilder = new VkImageViewBuilder(builder.getDevice());
        rebuild();
    }

    public void rebuild() {
        log.debug("Rebuilding swapchain");
        long oldSwapchain = swapchain;
        swapchain = builder.build(swapchain);

        if (oldSwapchain != VK14.VK_NULL_HANDLE)
            KHRSwapchain.vkDestroySwapchainKHR(builder.getDevice().device(), oldSwapchain, null);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int err;
            IntBuffer imageCount = stack.callocInt(1);
            err = KHRSwapchain.vkGetSwapchainImagesKHR(builder.getDevice().device(), swapchain, imageCount, null);
            if (err != VK14.VK_SUCCESS) throw new RuntimeException("Cannot get swapchain image count");
            LongBuffer images = stack.callocLong(imageCount.get(0));
            err = KHRSwapchain.vkGetSwapchainImagesKHR(builder.getDevice().device(), swapchain, imageCount, images);
            if (err != VK14.VK_SUCCESS) throw new RuntimeException("Cannot get swapchain images");

            this.imageCount = imageCount.get(0);

            List<VkImageContext> newImageViews = new ArrayList<>(this.imageCount);
            for (int i = 0; i < this.imageCount; i++) {
                long img = images.get(i);

                imageViewBuilder.setImage(img);
                imageViewBuilder.setImageFormat(builder.getImageFormat());
                imageViewBuilder.setImageViewType(VkImageViewType.TYPE_2D);
                imageViewBuilder.setVkImageSubresourceRange(new VkImageSubresourceRangeBuilder()
                        .setAspectMask(VkAspectMask.COLOR)
                        .setBaseLayer(0)
                        .setBaseMipLevel(0)
                        .setLayerCount(1)
                        .setLevelCount(1));

                newImageViews.add(i, imageViewBuilder.build());

            }

            replaceImageViews(newImageViews);

        }

    }

    private void replaceImageViews(List<VkImageContext> newImageViews) {
        List<VkImageContext> oldImageViews = this.image;
        this.image = newImageViews;
        for (VkImageContext imageContext : oldImageViews) {
            imageContext.destroyImageView();
        }
    }

    public long getSwapchain() {
        return swapchain;
    }

    public VkSwapchainBuilder getBuilder() {
        return builder;
    }

    public void destroy() {
        replaceImageViews(null);
        KHRSwapchain.vkDestroySwapchainKHR(builder.getDevice().device(), swapchain, null);
    }
}
