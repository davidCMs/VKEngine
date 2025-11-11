package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.window.GLFWWindow;
import org.lwjgl.vulkan.KHRSurface;

public final class VkGLFWSurface extends VkSurface {

    private final GLFWWindow window;

    public VkGLFWSurface(VkPhysicalDevice physicalDevice, GLFWWindow window) {
        super(physicalDevice);
        this.window = window;
        recreate();
    }

    @Override
    public void recreate() {
        destroy();
        surface = window.getVkSurface(physicalDevice.getInstance());
    }

    @Override
    public void destroy() {
        KHRSurface.vkDestroySurfaceKHR(physicalDevice.getInstance().instance(), surface, null);
    }
}
