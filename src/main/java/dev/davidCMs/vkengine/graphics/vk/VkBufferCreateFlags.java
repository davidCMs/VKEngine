package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkBufferCreateFlags {

    DEVICE_ADDRESS_CAPTURE_REPLAY(VK_BUFFER_CREATE_DEVICE_ADDRESS_CAPTURE_REPLAY_BIT),
    PROTECTED(VK_BUFFER_CREATE_PROTECTED_BIT),
    SPARSE_ALIASED(VK_BUFFER_CREATE_SPARSE_ALIASED_BIT),
    SPARSE_BINDING(VK_BUFFER_CREATE_SPARSE_BINDING_BIT),
    SPARSE_RESIDENCY(VK_BUFFER_CREATE_SPARSE_RESIDENCY_BIT),

    ;

    final int bit;

    VkBufferCreateFlags(int bit) {
        this.bit = bit;
    }

    public static long getMaskOf(VkBufferCreateFlags... bits) {
    	if (bits == null) return 0;

    	long sum = 0;
    	for (VkBufferCreateFlags bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }

    public static long getMaskOf(Iterable<VkBufferCreateFlags> bits) {
        if (bits == null) return 0;

        long sum = 0;
        for (VkBufferCreateFlags bit : bits) {
            if (bit == null) continue;
            sum |= bit.bit;
        }
        return sum;
    }

    public static boolean doesMaskHave(long mask, VkBufferCreateFlags bit) {
        if (bit == null) return false;
        return (mask & bit.bit) != 0;
    }

    public static Set<VkBufferCreateFlags> maskAsSet(long mask) {
        Set<VkBufferCreateFlags> set = new HashSet<>();
        for (VkBufferCreateFlags bit : values()) {
            if ((bit.bit & mask) != 0) set.add(bit);
        }
        return set;
    }

    public static VkBufferCreateFlags valueOf(long bitVal) {
        for (VkBufferCreateFlags bit : values()) {
            if (bit.bit == bitVal) return bit;
        }
        throw new IllegalArgumentException("No Value for bit: " + bitVal);
    }
}
