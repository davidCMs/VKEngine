package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.vulkan.VK14;

public abstract class VkSemaphore {

    abstract long getSemaphore();

    abstract public void destroy();

}
