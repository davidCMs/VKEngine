package dev.davidCMs.vkengine.vk;

import java.util.Collection;

import static org.lwjgl.vulkan.VK14.*;

public enum VkPipelineCreateFlags {

	DISPATCH_BASE(VK_PIPELINE_CREATE_DISPATCH_BASE_BIT),
	ALLOW_DERIVATIVES(VK_PIPELINE_CREATE_ALLOW_DERIVATIVES_BIT),
	DISABLE_OPTIMIZATION(VK_PIPELINE_CREATE_DISABLE_OPTIMIZATION_BIT),
	EARLY_RETURN_ON_FAILURE(VK_PIPELINE_CREATE_EARLY_RETURN_ON_FAILURE_BIT),
	FAIL_ON_PIPELINE_COMPILE_REQUIRED(VK_PIPELINE_CREATE_FAIL_ON_PIPELINE_COMPILE_REQUIRED_BIT),
	NO_PROTECTED_ACCESS(VK_PIPELINE_CREATE_NO_PROTECTED_ACCESS_BIT),
	PROTECTED_ACCESS_ONLY(VK_PIPELINE_CREATE_PROTECTED_ACCESS_ONLY_BIT),
	VIEW_INDEX_FROM_DEVICE_INDEX(VK_PIPELINE_CREATE_VIEW_INDEX_FROM_DEVICE_INDEX_BIT),

	;

	final int bit;

	VkPipelineCreateFlags(int bit) {
		this.bit = bit;
	}

	public static int getMaskOf(VkPipelineCreateFlags... bits) {
		if (bits == null) return 0;

		int sum = 0;
		for (VkPipelineCreateFlags bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getMaskOf(Collection<VkPipelineCreateFlags> bits) {
		return getMaskOf(bits.toArray(new VkPipelineCreateFlags[0]));
	}
}
