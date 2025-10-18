package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkFenceCreateFlags {

    SIGNALED(VK_FENCE_CREATE_SIGNALED_BIT)

    ;

    final int bit;

    VkFenceCreateFlags(int bit) {
        this.bit = bit;
    }

    public static long getMaskOf(VkFenceCreateFlags... bits) {
    	if (bits == null) return 0;

    	long sum = 0;
    	for (VkFenceCreateFlags bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }

    public static long getMaskOf(Iterable<VkFenceCreateFlags> bits) {
        if (bits == null) return 0;

        long sum = 0;
        for (VkFenceCreateFlags bit : bits) {
            if (bit == null) continue;
            sum |= bit.bit;
        }
        return sum;
    }

    public static boolean doesMaskHave(long mask, VkFenceCreateFlags bit) {
        if (bit == null) return false;
        return (mask & bit.bit) != 0;
    }

    public static Set<VkFenceCreateFlags> maskAsSet(long mask) {
        Set<VkFenceCreateFlags> set = new HashSet<>();
        for (VkFenceCreateFlags bit : values()) {
            if ((bit.bit & mask) != 0) set.add(bit);
        }
        return set;
    }

    public static VkFenceCreateFlags valueOf(long bitVal) {
        for (VkFenceCreateFlags bit : values()) {
            if (bit.bit == bitVal) return bit;
        }
        throw new IllegalArgumentException("No Value for bit: " + bitVal);
    }

}
