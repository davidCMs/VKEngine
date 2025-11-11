package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.Destroyable;

public sealed abstract class VkSurface implements Destroyable permits VkGLFWSurface {

    protected long surface;
    protected final VkPhysicalDevice physicalDevice;

    protected VkSurface(VkPhysicalDevice physicalDevice) {
        this.physicalDevice = physicalDevice;
    }

    public abstract void recreate();

    public VkSurfaceInfo getSurfaceInfo() {
        return VkSurfaceInfo.getFrom(physicalDevice, surface);
    }

    public long getSurface() {
        return surface;
    }

}
