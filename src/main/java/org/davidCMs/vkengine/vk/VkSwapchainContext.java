package org.davidCMs.vkengine.vk;

public class VkSwapchainContext {

    private long swapchain;
    private final VkSwapchainBuilder builder;

    public VkSwapchainContext(VkSwapchainBuilder builder) {
        this.builder = builder;
        swapchain = builder.build(swapchain);
    }

}
