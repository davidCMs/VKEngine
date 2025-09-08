package dev.davidCMs.vkengine.vk;

import java.util.Collection;

import static org.lwjgl.vulkan.VK14.*;

public enum VkVertexInputRate {

    INSTANCE(VK_VERTEX_INPUT_RATE_INSTANCE),
    VERTEX(VK_VERTEX_INPUT_RATE_VERTEX)

    ;

    final int bit;

    VkVertexInputRate(int bit) {
        this.bit = bit;
    }

    public static int getMaskOf(VkVertexInputRate... bits) {
    	if (bits == null) return 0;

    	int sum = 0;
    	for (VkVertexInputRate bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }

    public static int getMaskOf(Collection<VkVertexInputRate> bits) {
        if (bits == null) return 0;
    	return getMaskOf(bits.toArray(new VkVertexInputRate[0]));
    }
}
