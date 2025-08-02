package org.davidCMs.vkengine.vk;

import java.util.Collection;

import static org.lwjgl.vulkan.VK14.*;

public enum VkFenceCreateFlags {

    SIGNALED(VK_FENCE_CREATE_SIGNALED_BIT)

    ;

    final int bit;

    VkFenceCreateFlags(int bit) {
        this.bit = bit;
    }

    public static int getMaskOf(VkFenceCreateFlags... bits) {
    	if (bits == null) return 0;

    	int sum = 0;
    	for (VkFenceCreateFlags bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }

    public static int getMaskOf(Collection<VkFenceCreateFlags> bits) {
        if (bits == null) return 0;
        return getMaskOf(bits.toArray(new VkFenceCreateFlags[0]));
    }

}
