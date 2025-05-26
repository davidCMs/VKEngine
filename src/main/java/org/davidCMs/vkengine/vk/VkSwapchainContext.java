package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.KHRSwapchain;

public class VkSwapchainContext {

    private long swapchain = 0;
    private final VkSwapchainBuilder builder;

    public VkSwapchainContext(VkSwapchainBuilder builder) {
        this.builder = builder;
        rebuild();
    }

    public void rebuild() {
        swapchain = builder.build(swapchain);
    }

    public long getSwapchain() {
        return swapchain;
    }

    public VkSwapchainBuilder getBuilder() {
        return builder;
    }

    public void destroy() {
        KHRSwapchain.vkDestroySwapchainKHR(builder.getDevice(), swapchain, null);
    }
}
