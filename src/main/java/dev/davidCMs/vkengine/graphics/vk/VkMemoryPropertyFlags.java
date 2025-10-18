package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    public static long getMaskOf(VkMemoryPropertyFlags... bits) {
    	if (bits == null) return 0;

    	long sum = 0;
    	for (VkMemoryPropertyFlags bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }

    public static long getMaskOf(Iterable<VkMemoryPropertyFlags> bits) {
        if (bits == null) return 0;

        long sum = 0;
        for (VkMemoryPropertyFlags bit : bits) {
            if (bit == null) continue;
            sum |= bit.bit;
        }
        return sum;
    }

    public static boolean doesMaskHave(long mask, VkMemoryPropertyFlags bit) {
        if (bit == null) return false;
        return (mask & bit.bit) != 0;
    }

    public static Set<VkMemoryPropertyFlags> maskAsSet(long mask) {
        Set<VkMemoryPropertyFlags> set = new HashSet<>();
        for (VkMemoryPropertyFlags bit : values()) {
            if ((bit.bit & mask) != 0) set.add(bit);
        }
        return set;
    }

    public static VkMemoryPropertyFlags valueOf(long bitVal) {
        for (VkMemoryPropertyFlags bit : values()) {
            if (bit.bit == bitVal) return bit;
        }
        throw new IllegalArgumentException("No Value for bit: " + bitVal);
    }

}
