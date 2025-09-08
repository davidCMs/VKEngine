package dev.davidCMs.vkengine.vk;

import java.util.Collection;

import static org.lwjgl.vulkan.VK14.*;

public enum VkBufferUsageFlags {

    INDEX_BUFFER(VK_BUFFER_USAGE_2_INDEX_BUFFER_BIT),
    INDIRECT_BUFFER(VK_BUFFER_USAGE_2_INDIRECT_BUFFER_BIT),
    STORAGE_BUFFER(VK_BUFFER_USAGE_2_STORAGE_BUFFER_BIT),
    SHADER_DEVICE_ADDRESS(VK_BUFFER_USAGE_2_SHADER_DEVICE_ADDRESS_BIT),
    UNIFORM_BUFFER(VK_BUFFER_USAGE_2_UNIFORM_BUFFER_BIT),
    STORAGE_TEXEL_BUFFER(VK_BUFFER_USAGE_2_STORAGE_TEXEL_BUFFER_BIT),
    TRANSFER_DST(VK_BUFFER_USAGE_2_TRANSFER_DST_BIT),
    TRANSFER_SRC(VK_BUFFER_USAGE_2_TRANSFER_SRC_BIT),
    UNIFORM_TEXEL_BUFFER(VK_BUFFER_USAGE_2_UNIFORM_TEXEL_BUFFER_BIT),
    VERTEX_BUFFER(VK_BUFFER_USAGE_2_VERTEX_BUFFER_BIT),

    ;

    final long bit;

    VkBufferUsageFlags(long bit) {
        this.bit = bit;
    }
    
    public static long getMaskOf(VkBufferUsageFlags... bits) {
    	if (bits == null) return 0;
    	
    	long sum = 0;
    	for (VkBufferUsageFlags bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }
    
    public static long getMaskOf(Collection<VkBufferUsageFlags> bits) {
        if (bits == null) return 0;
        return getMaskOf(bits.toArray(new VkBufferUsageFlags[0]));
    }
    
}
