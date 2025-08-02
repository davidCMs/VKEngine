package org.davidCMs.vkengine.vk;

import java.util.Collection;

import static org.lwjgl.vulkan.VK14.*;

public enum VkMemoryPropertyFlags {

    DEVICE_LOCAL(VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT),
    HOST_CACHED(VK_MEMORY_PROPERTY_HOST_CACHED_BIT),
    HOST_COHERENT(VK_MEMORY_PROPERTY_HOST_COHERENT_BIT),
    HOST_VISIBLE(VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT),
    PROTECTED(VK_MEMORY_PROPERTY_PROTECTED_BIT),
    LAZILY_ALLOCATED(VK_MEMORY_PROPERTY_LAZILY_ALLOCATED_BIT),

    ;

    final int bit;

    VkMemoryPropertyFlags(int bit) {
        this.bit = bit;
    }

    public static int getMaskOf(VkMemoryPropertyFlags... bits) {
    	if (bits == null) return 0;

    	int sum = 0;
    	for (VkMemoryPropertyFlags bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }

    public static int getMaskOf(Collection<VkMemoryPropertyFlags> bits) {
        if (bits == null) return 0;
        return getMaskOf(bits.toArray(new VkMemoryPropertyFlags[0]));
    }
}
