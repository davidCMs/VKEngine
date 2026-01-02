package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.vulkan.VK14;

import java.util.HashSet;
import java.util.Set;

public enum VkDescriptionPoolCreateFlags {

    FREE_DESCRIPTOR_SET(VK14.VK_DESCRIPTOR_POOL_CREATE_FREE_DESCRIPTOR_SET_BIT),
    UPDATE_AFTER_BIND(VK14.VK_DESCRIPTOR_POOL_CREATE_UPDATE_AFTER_BIND_BIT)

    ;

    final int bit;

    VkDescriptionPoolCreateFlags(int bit) {
        this.bit = bit;
    }

    public static long getMaskOf(VkDescriptionPoolCreateFlags... bits) {
    	if (bits == null) return 0;

    	long sum = 0;
    	for (VkDescriptionPoolCreateFlags bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }

    public static long getMaskOf(Iterable<VkDescriptionPoolCreateFlags> bits) {
        if (bits == null) return 0;

        long sum = 0;
        for (VkDescriptionPoolCreateFlags bit : bits) {
            if (bit == null) continue;
            sum |= bit.bit;
        }
        return sum;
    }

    public static boolean doesMaskHave(long mask, VkDescriptionPoolCreateFlags bit) {
        if (bit == null) return false;
        return (mask & bit.bit) != 0;
    }

    public static Set<VkDescriptionPoolCreateFlags> maskAsSet(long mask) {
        Set<VkDescriptionPoolCreateFlags> set = new HashSet<>();
        for (VkDescriptionPoolCreateFlags bit : values()) {
            if ((bit.bit & mask) != 0) set.add(bit);
        }
        return set;
    }

    public static VkDescriptionPoolCreateFlags valueOf(long bitVal) {
        for (VkDescriptionPoolCreateFlags bit : values()) {
            if (bit.bit == bitVal) return bit;
        }
        throw new IllegalArgumentException("No Value for bit: " + bitVal);
    }

}
