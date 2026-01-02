package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.Destroyable;
import org.lwjgl.vulkan.VK14;

public record VkDescriptorSetLayout(long layout, VkDeviceContext device) implements Destroyable {
    @Override
    public void destroy() {
        VK14.vkDestroyDescriptorSetLayout(device.device(), layout, null);
    }
}
