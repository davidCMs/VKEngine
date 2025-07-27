package org.davidCMs.vkengine.vk;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkBufferMemoryRequirementsInfo2;
import org.lwjgl.vulkan.VkMemoryRequirements2;

import java.util.Set;

public class VkBuffer {

    private final long buffer;
    private final VkDeviceContext device;
    private final Set<VkBufferUsageFlags> usage;
    private final VkMemoryRequirements memoryRequirements;

    public VkBuffer(long buffer, @NotNull VkDeviceContext device, Set<VkBufferUsageFlags> usage) {
        this.buffer = buffer;
        this.device = device;
        this.usage = usage;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferMemoryRequirementsInfo2 info = VkBufferMemoryRequirementsInfo2.calloc(stack);
            info.sType$Default();
            info.buffer(buffer);

            VkMemoryRequirements2 requirements = VkMemoryRequirements2.calloc(stack);
            VK14.vkGetBufferMemoryRequirements2(device.device(), info, requirements);

            requirements.memoryRequirements().alignment();

            this.memoryRequirements = new VkMemoryRequirements(
                    requirements.memoryRequirements().size(),
                    requirements.memoryRequirements().alignment(),
                    requirements.memoryRequirements().memoryTypeBits()
            );
        }
    }

    long getBuffer() {
        return buffer;
    }

    public VkDeviceContext getDevice() {
        return device;
    }

    public Set<VkBufferUsageFlags> getUsage() {
        return usage;
    }

    public VkMemoryRequirements getMemoryRequirements() {
        return memoryRequirements;
    }
}
