package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.common.AutoCloseableByteBuffer;
import org.davidCMs.vkengine.util.LogUtils;
import org.davidCMs.vkengine.util.VkUtils;
import org.davidCMs.vkengine.vk.VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.*;

import org.davidCMs.vkengine.vk.VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.VkMemoryHeap;
import org.davidCMs.vkengine.vk.VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.VkMemoryType;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MathUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.util.Set;

public class VkBuffer {

    private final long buffer;
    private final VkDeviceContext device;
    private final Set<VkBufferUsageFlags> usage;
    private final VkMemoryRequirements memoryRequirements;

    private long bufferMemory;
    private boolean cpuAccessible;
    private boolean isCoherent;

    public VkBuffer(long buffer, VkDeviceContext device, Set<VkBufferUsageFlags> usage) {
        this.buffer = buffer;
        this.device = device;
        this.usage = usage;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferMemoryRequirementsInfo2 info = VkBufferMemoryRequirementsInfo2.calloc(stack);
            info.sType$Default();
            info.buffer(buffer);

            VkMemoryRequirements2 requirements = VkMemoryRequirements2.calloc(stack);
            requirements.sType$Default();
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
        boolean isBestCoherent = false;

        int bestFallbackIndex = -1;
        long bestFallbackHeapSize = 0;
        boolean isBestFallbackCoherent = false;

        for (int i = 0; i < info.memoryProperties().getMemoryTypeCount(); i++) {
            if ((memoryRequirements.memoryTypeBits() & (1 << i)) == 0)
                continue;

            VkMemoryType type = info.memoryProperties().getMemoryType(i);
            VkMemoryHeap heap = info.memoryProperties().getMemoryHeap(type.heapIndex());

            boolean deviceLocal = VkMemoryPropertyFlags.doesMaskHave(type.propertyFlags(), VkMemoryPropertyFlags.DEVICE_LOCAL);
            boolean hostVisible = VkMemoryPropertyFlags.doesMaskHave(type.propertyFlags(), VkMemoryPropertyFlags.HOST_VISIBLE);
            boolean hostCoherent = VkMemoryPropertyFlags.doesMaskHave(type.propertyFlags(), VkMemoryPropertyFlags.HOST_COHERENT);

            if (!hostVisible)
                continue;

            if (!deviceLocal && heap.size() > bestHeapSize) {
                bestIndex = i;
                bestHeapSize = heap.size();
                isBestCoherent = hostCoherent;
            }

            if (heap.size() > bestFallbackHeapSize) {
                bestFallbackIndex = i;
                bestFallbackHeapSize = heap.size();
                isBestFallbackCoherent = hostCoherent;
            }
        }

        if (bestIndex == -1)
            if (bestFallbackIndex == -1)
                throw new RuntimeException("Failed to find CPU memory... for is computer has the ram?");
            else {
                isCoherent = isBestFallbackCoherent;
                cpuAccessible = true;
                return allocateMemory(bestFallbackIndex);
            }
        else {
            isCoherent = isBestCoherent;
            cpuAccessible = true;
            return allocateMemory(bestIndex);
        }
    }

    public VkBuffer allocateGPUMemory() {
        if (hasAllocatedMemory())
            throw new RuntimeException("Memory already allocated");

        VkPhysicalDeviceInfo info = device.physicalDeviceInfo();

        int bestIndex = -1;
        long bestHeapSize = 0;

        int bestFallbackIndex = -1;
        long bestFallbackHeapSize = 0;
        boolean isBestFallbackHostVisible = false;
        boolean isBestFallbackCoherent = false;

        for (int i = 0; i < info.memoryProperties().getMemoryTypeCount(); i++) {
            if ((memoryRequirements.memoryTypeBits() & (1 << i)) == 0)
                continue;

            VkMemoryType type = info.memoryProperties().getMemoryType(i);
            VkMemoryHeap heap = info.memoryProperties().getMemoryHeap(type.heapIndex());

            boolean deviceLocal = VkMemoryPropertyFlags.doesMaskHave(type.propertyFlags(), VkMemoryPropertyFlags.DEVICE_LOCAL);
            boolean hostVisible = VkMemoryPropertyFlags.doesMaskHave(type.propertyFlags(), VkMemoryPropertyFlags.HOST_VISIBLE);
            boolean hostCoherent = VkMemoryPropertyFlags.doesMaskHave(type.propertyFlags(), VkMemoryPropertyFlags.HOST_COHERENT);

            if (deviceLocal && !hostVisible && heap.size() > bestHeapSize) {
                bestIndex = i;
                bestHeapSize = heap.size();
            }

            if (deviceLocal && heap.size() > bestFallbackHeapSize) {
                bestFallbackIndex = i;
                bestFallbackHeapSize = heap.size();
                isBestFallbackHostVisible = hostVisible;
                isBestFallbackCoherent = hostCoherent;
            }
        }

        if (bestIndex == -1)
            if (bestFallbackIndex == -1)
                throw new RuntimeException("Failed to find GPU memory... also failed to find fallback... for is computer has the ram?");
            else {
                cpuAccessible = isBestFallbackHostVisible;
                isCoherent = isBestFallbackCoherent;
                return allocateMemory(bestFallbackIndex);
            }
        else {
            cpuAccessible = false;
            isCoherent = false;
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

    public VkBuffer uploadData(AutoCloseableByteBuffer data, boolean flush) {
        return uploadData(data.unwrap(), flush);
    }

    public VkBuffer uploadData(ByteBuffer data, boolean flush) {
        if (!isCpuAccessible())
            throw new RuntimeException("Cannot upload as buffer memory is not cpu accessible");
        if (data.order() == ByteOrder.BIG_ENDIAN)
            throw new IllegalArgumentException("data must be in BIG-ENDIAN order");
        long size = getSize();
        if (data.remaining() != size)
            throw new IllegalArgumentException("data must be the same size as buffer");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pointer = stack.callocPointer(1);

            /*
            VkMemoryMapInfo info = VkMemoryMapInfo.calloc(stack);
            info.sType$Default();
            info.size(size);
            info.offset(0);
            info.memory(bufferMemory);

             */

            int err;
            //err = VK14.vkMapMemory2(device.device(), info, pointer);
            err = VK14.vkMapMemory(device.device(), bufferMemory, 0, size, 0, pointer);
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("Failed to map buffer memory: " + VkUtils.translateErrorCode(err));

            long src = MemoryUtil.memAddress(data);
            long dst = pointer.get(0);

            MemoryUtil.memCopy(src, dst, size);

            if (!isCoherent && flush) {
                long atom = device.physicalDeviceInfo().properties().limits().nonCoherentAtomSize();

                long alignedSize = ((size + atom - 1) / atom) * atom;
                alignedSize = Math.min(alignedSize, size);

                VkMappedMemoryRange rangeInfo = VkMappedMemoryRange.calloc(stack);
                rangeInfo.sType$Default();
                rangeInfo.memory(bufferMemory);
                rangeInfo.offset(0);
                rangeInfo.size(alignedSize);


                err = VK14.vkFlushMappedMemoryRanges(device.device(), rangeInfo);
                if (err != VK14.VK_SUCCESS)
                    throw new RuntimeException("Failed to flush mapped memory: " + VkUtils.translateErrorCode(err));
            }

            VkMemoryUnmapInfo unmapInfo = VkMemoryUnmapInfo.calloc(stack);
            unmapInfo.sType$Default();
            unmapInfo.memory(bufferMemory);

            VK14.vkUnmapMemory(device.device(), bufferMemory);
            /*err = VK14.vkUnmapMemory2(device.device(), unmapInfo);
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("Failed to unmap buffer memory: " + VkUtils.translateErrorCode(err));
            */
        }

        return this;
    }

    public AutoCloseableByteBuffer createPreConfiguredByteBuffer() {
        return new AutoCloseableByteBuffer((int)getSize()).order(ByteOrder.LITTLE_ENDIAN);
    }

    public long getSize() {
        return memoryRequirements.size();
    }

    public void destroy() {
        VK14.vkFreeMemory(device.device(), bufferMemory, null);
        VK14.vkDestroyBuffer(device.device(), buffer, null);
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
