package dev.davidCMs.vkengine.vk;

import java.util.Collection;

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

    public static int getMaskOf(VkBufferCreateFlags... bits) {
    	if (bits == null) return 0;

    	int sum = 0;
    	for (VkBufferCreateFlags bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }

    public static int getMaskOf(Collection<VkBufferCreateFlags> bits) {
        if (bits == null) return 0;
        return getMaskOf(bits.toArray(new VkBufferCreateFlags[0]));
    }
}
