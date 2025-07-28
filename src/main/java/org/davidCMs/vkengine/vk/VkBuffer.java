package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.VkUtils;
import org.davidCMs.vkengine.vk.VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.*;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkBufferMemoryRequirementsInfo2;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements2;

import java.nio.LongBuffer;
import java.util.Set;

public class VkBuffer {

    private final long buffer;
    private final VkDeviceContext device;
    private final Set<VkBufferUsageFlags> usage;
    private final VkMemoryRequirements memoryRequirements;

    private long bufferMemory;
    private boolean cpuAccessible;

    public VkBuffer(long buffer, VkDeviceContext device, Set<VkBufferUsageFlags> usage) {
        this.buffer = buffer;
        this.device = device;
        this.usage = usage;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferMemoryRequirementsInfo2 info = VkBufferMemoryRequirementsInfo2.calloc(stack);
            info.sType$Default();
            info.buffer(buffer);

            VkMemoryRequirements2 requirements = VkMemoryRequirements2.calloc(stack);
            VK14.vkGetBufferMemoryRequirements2(device.device(), info, requirements);

            this.memoryRequirements = new VkMemoryRequirements(
                    requirements.memoryRequirements().size(),
                    requirements.memoryRequirements().alignment(),
                    requirements.memoryRequirements().memoryTypeBits()
            );
        }
    }

    public VkBuffer allocateCPUMemory() {
        if (hasAllocatedMemory())
            throw new RuntimeException("Memory already allocated");

        VkPhysicalDeviceInfo info = device.physicalDeviceInfo();

        int bestIndex = -1;
        long bestHeapSize = 0;

        for (int i = 0; i < info.memoryProperties().getMemoryTypeCount(); i++) {
            if ((memoryRequirements.memoryTypeBits() & (1 << i)) == 0)
                continue;

            VkMemoryType type = info.memoryProperties().getMemoryType(i);
            VkMemoryHeap heap = info.memoryProperties().getMemoryHeap(type.heapIndex());

            if ((type.propertyFlags() & VkMemoryPropertyFlags.DEVICE_LOCAL.bit) != 0)
                continue;

            if ((type.propertyFlags() & VkMemoryPropertyFlags.HOST_VISIBLE.bit) == 0)
                continue;

            if (heap.size() > bestHeapSize) {
                bestIndex = i;
                bestHeapSize = heap.size();
            }
        }

        if (bestIndex == -1)
            throw new RuntimeException("Failed to find CPU memory... for is computer has the ram?");

        cpuAccessible = true;

        return allocateMemory(bestIndex);
    }

    public VkBuffer allocateGPUMemory() {
        if (hasAllocatedMemory())
            throw new RuntimeException("Memory already allocated");

        VkPhysicalDeviceInfo info = device.physicalDeviceInfo();

        int bestIndex = -1;
        long bestHeapSize = 0;

        int bestFallbackIndex = -1;
        long bestFallbackHeapSize = 0;

        for (int i = 0; i < info.memoryProperties().getMemoryTypeCount(); i++) {
            if ((memoryRequirements.memoryTypeBits() & (1 << i)) == 0)
                continue;

            VkMemoryType type = info.memoryProperties().getMemoryType(i);
            VkMemoryHeap heap = info.memoryProperties().getMemoryHeap(type.heapIndex());

            boolean deviceLocal = ((type.propertyFlags() & VkMemoryPropertyFlags.DEVICE_LOCAL.bit) != 0);
            boolean hostVisible = ((type.propertyFlags() & VkMemoryPropertyFlags.HOST_VISIBLE.bit) != 0);

            if (deviceLocal && hostVisible) {
                if (heap.size() > bestFallbackHeapSize) {
                    bestFallbackIndex = i;
                    bestFallbackHeapSize = heap.size();
                }
                continue;
            }

            if (hostVisible)
                continue;

            if (heap.size() > bestHeapSize) {
                bestIndex = i;
                bestHeapSize = heap.size();
            }

        }

        if (bestIndex == -1)
            if (bestFallbackIndex == -1)
                throw new RuntimeException("Failed to find GPU memory... also failed to find fallback... for is computer has the ram?");
            else {
                cpuAccessible = true;
                return allocateMemory(bestFallbackIndex);
            }
        else {
            cpuAccessible = false;
            return allocateMemory(bestIndex);
        }
    }

    public VkBuffer allocateMemory(int memoryTypeIndex) {
        if (hasAllocatedMemory())
            throw new RuntimeException("Memory already allocated");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkMemoryAllocateInfo info = VkMemoryAllocateInfo.calloc(stack);
            info.sType$Default();
            info.allocationSize(memoryRequirements.size());
            info.memoryTypeIndex(memoryTypeIndex);

            LongBuffer lb = stack.mallocLong(1);
            int err;
            err = VK14.vkAllocateMemory(device.device(), info, null, lb);
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("failed to allocate buffer memory: " + VkUtils.translateErrorCode(err));

            this.bufferMemory = lb.get(0);

            err = VK14.vkBindBufferMemory(device.device(), buffer, bufferMemory, 0);
            if (err != VK14.VK_SUCCESS) {
                this.bufferMemory = VK14.VK_NULL_HANDLE;
                throw new RuntimeException("failed to bind buffer memory: " + VkUtils.translateErrorCode(err));
            }

        }
        return this;
    }

    public boolean hasAllocatedMemory() {
        return bufferMemory != VK14.VK_NULL_HANDLE;
    }

    public boolean isCpuAccessible() {
        return cpuAccessible;
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
