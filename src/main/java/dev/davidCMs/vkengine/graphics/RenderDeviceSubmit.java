package dev.davidCMs.vkengine.graphics;

import dev.davidCMs.vkengine.graphics.vk.VkCommandPool;
import dev.davidCMs.vkengine.graphics.vk.VkQueue;

@FunctionalInterface
public interface RenderDeviceSubmit {

    VkQueue.VkSubmitInfoBuilder[] submit(ThreadLocal<VkCommandPool> commandPool);

}
