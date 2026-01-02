package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.NativeByteBuffer;

import dev.davidCMs.vkengine.common.Destroyable;
import dev.davidCMs.vkengine.util.LogUtils;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationInfo;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteOrder;
import java.util.Set;

public class VkBuffer implements Destroyable {

    private final static TaggedLogger log = Logger.tag("Vulkan");

    private final Object lock = new Object();

    private final long buffer;
    private final VkDeviceContext device;
    private final Set<VkBufferUsageFlags> usage;

    private final long allocation;
    private final long size;

    private final long bufferMemory;
    private final VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.VkMemoryType memoryType;

    private final boolean hostVisible;
    private final boolean hostCoherent;

    public VkBuffer(long buffer,
                    VkDeviceContext device,
                    Set<VkBufferUsageFlags> usage,
                    long allocation,
                    long size,
                    VmaAllocationInfo info) {
        this.buffer = buffer;
        this.device = device;
        this.usage = usage;
        this.allocation = allocation;
        this.size = size;
        this.memoryType = device.physicalDevice().getInfo().memoryProperties().getMemoryType(info.memoryType());
        this.bufferMemory = info.deviceMemory();
        this.hostVisible = VkMemoryPropertyFlags.doesMaskHave(this.memoryType.propertyFlags(), VkMemoryPropertyFlags.HOST_VISIBLE);
        this.hostCoherent = VkMemoryPropertyFlags.doesMaskHave(this.memoryType.propertyFlags(), VkMemoryPropertyFlags.HOST_COHERENT);
    }

    public void writeData(NativeByteBuffer data) {
        writeData(data, 0, data.getSize());
    }

    public void writeData(NativeByteBuffer data, long offset) {
        writeData(data, offset, data.getSize());
    }

    public void writeData(NativeByteBuffer data, long offset, long size) {
        synchronized (lock) {
            long mappedData = mapMemory();

            if (data.order() != ByteOrder.LITTLE_ENDIAN)
                throw new RuntimeException("data must have a Little Endian byte order");
            if (size > data.getSize()) throw new RuntimeException("size is bigger than the capacity of data");
            if (offset + size > this.size)
                throw new RuntimeException("data at current offset(" + offset + ") wont fit into the buffer");

            //log.info("byteBuffer address = {}, mapped data = {}, buffer size = {}, offset = {}, copy size = {}",
            //        LogUtils.asHex(data.getAddress()), LogUtils.asHex(mappedData), data.getSize(), offset, size);

            data.copyTo(mappedData + offset, size);

            if (!hostCoherent)
                Vma.vmaFlushAllocation(device.allocator(), allocation, offset, size);
        }
    }

    public NativeByteBuffer createPreConfiguredByteBuffer() {
        return NativeByteBuffer.malloc((int) getSize(), ByteOrder.LITTLE_ENDIAN);
    }

    public long getSize() {
        return size;
    }

    public void destroy() {
        synchronized (lock) {
            Vma.vmaDestroyBuffer(device.allocator(), buffer, allocation);
        }
    }

    public long mapMemory() {
        synchronized (lock) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                VmaAllocationInfo info = getAllocInfo(stack);
                if (!hostVisible) throw new RuntimeException("Cannot map as memory is not host visible");

                if (info.pMappedData() != VK14.VK_NULL_HANDLE)
                    return info.pMappedData();

                PointerBuffer pb = stack.mallocPointer(1);
                int err = Vma.vmaMapMemory(device.allocator(), allocation, pb);
                if (!VkUtils.successful(err))
                    throw new RuntimeException("Failed to map memory err:" + VkUtils.translateErrorCode(err));
                return pb.get(0);
            }
        }
    }

    public VkBuffer unmapMemory() {
        synchronized (lock) {
            Vma.vmaUnmapMemory(device.allocator(), allocation);
            return this;
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

    public boolean isHostCoherent() {
        return hostCoherent;
    }

    public boolean isHostVisible() {
        return hostVisible;
    }

    public String getName() {
        try (VmaAllocationInfo info = getAllocInfo()) {
            return info.pNameString();
        }
    }

    private VmaAllocationInfo getAllocInfo() {
        return getAllocInfo(null);
    }

    private VmaAllocationInfo getAllocInfo(MemoryStack stack) {
        VmaAllocationInfo allocInfo;
        if (stack != null)
            allocInfo = VmaAllocationInfo.malloc(stack);
        else
            allocInfo = VmaAllocationInfo.malloc();
        Vma.vmaGetAllocationInfo(device.allocator(), allocation, allocInfo);
        return allocInfo;
    }

    public VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.VkMemoryType getMemoryType() {
        return memoryType;
    }
}
