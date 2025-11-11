package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.AutoCloseableByteBuffer;

import dev.davidCMs.vkengine.common.Destroyable;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

public class VkBuffer implements Destroyable {

    private final static TaggedLogger log = Logger.tag("Vulkan");

    private final long buffer;
    private final VkDeviceContext device;
    private final Set<VkBufferUsageFlags> usage;

    private final long allocation;
    private final long size;

    private final long bufferMemory;
    private final VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.VkMemoryType memoryType;
    private final String name;

    private final boolean hostVisible;
    private final boolean hostCoherent;

    private long mappedData;

    public VkBuffer(long buffer,
                    VkDeviceContext device,
                    Set<VkBufferUsageFlags> usage,
                    long allocation,
                    long size,
                    long bufferMemory,
                    int memoryType,
                    long mappedData,
                    String name) {
        this.buffer = buffer;
        this.device = device;
        this.usage = usage;
        this.allocation = allocation;
        this.size = size;
        this.bufferMemory = bufferMemory;
        this.memoryType = device.physicalDevice().getInfo().memoryProperties().getMemoryType(memoryType);
        this.mappedData = mappedData;
        this.name = name;
        this.hostVisible = VkMemoryPropertyFlags.doesMaskHave(this.memoryType.propertyFlags(), VkMemoryPropertyFlags.HOST_VISIBLE);
        this.hostCoherent = VkMemoryPropertyFlags.doesMaskHave(this.memoryType.propertyFlags(), VkMemoryPropertyFlags.HOST_COHERENT);
    }

    public void writeData(ByteBuffer data) {
        writeData(data, 0, data.capacity());
    }

    public void writeData(ByteBuffer data, long offset) {
        writeData(data, offset, data.capacity());
    }

    public void writeData(ByteBuffer data, long offset, long size) {
        if (!isMemoryMapped()) mapMemory();
        if (data.order() != ByteOrder.LITTLE_ENDIAN) throw new RuntimeException("data must have a Little Endian byte order");
        if (size > data.capacity()) throw new RuntimeException("size is bigger than the capacity of data");
        if (offset + size > this.size) throw new RuntimeException("data at current offset(" + offset + ") wont fit into the buffer");

        MemoryUtil.memCopy(MemoryUtil.memAddress(data), mappedData + offset, size);

        if (!hostCoherent)
            Vma.vmaFlushAllocation(device.allocator(), allocation, offset, size);
    }

    public AutoCloseableByteBuffer createPreConfiguredByteBuffer() {
        return new AutoCloseableByteBuffer((int)getSize()).order(ByteOrder.LITTLE_ENDIAN);
    }

    public long getSize() {
        return size;
    }

    public void destroy() {
        if (isMemoryMapped()) unmapMemory();
        Vma.vmaDestroyBuffer(device.allocator(), buffer, allocation);
    }

    public VkBuffer mapMemory() {
        if (isMemoryMapped()) return this;
        if (!hostVisible) throw new RuntimeException("Cannot map as memory is not host visible");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pb = stack.mallocPointer(1);
            int err = Vma.vmaMapMemory(device.allocator(), allocation, pb);
            if (!VkUtils.successful(err))
                throw new RuntimeException("Failed to map memory err:" + VkUtils.translateErrorCode(err));
            mappedData = pb.get(0);
        }
        return this;
    }

    public VkBuffer unmapMemory() {
        Vma.vmaUnmapMemory(device.allocator(), allocation);
        this.mappedData = VK14.VK_NULL_HANDLE;
        return this;
    }

    private boolean isMemoryMapped() {
        return mappedData != VK14.VK_NULL_HANDLE;
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
        return name;
    }

    public VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.VkMemoryType getMemoryType() {
        return memoryType;
    }
}
