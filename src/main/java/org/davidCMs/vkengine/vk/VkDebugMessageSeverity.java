package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.EXTDebugUtils;

import java.util.Collection;

public enum VkDebugMessageSeverity {

	VERBOSE(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT),
	INFO(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT),
	WARNING(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT),
	ERROR(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT)

	;

	final int bit;

	VkDebugMessageSeverity(int bit) {
		this.bit = bit;
	}

	static int getMaskOf(VkDebugMessageSeverity... bits) {
		if (bits == null)
			return 0;

		int sum = 0;
		for (VkDebugMessageSeverity bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getMaskOf(Collection<VkDebugMessageSeverity> bits) {
		return getMaskOf(bits.toArray(new VkDebugMessageSeverity[0]));
	}
}
