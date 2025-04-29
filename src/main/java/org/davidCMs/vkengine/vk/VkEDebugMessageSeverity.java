package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.EXTDebugUtils;

public enum VkEDebugMessageSeverity {

	VERBOSE(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT),
	INFO(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT),
	WARNING(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT),
	ERROR(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT)

	;

	final int bit;

	VkEDebugMessageSeverity(int bit) {
		this.bit = bit;
	}

	static int getValueOf(VkEDebugMessageSeverity... bits) {
		int sum = 0;
		for (VkEDebugMessageSeverity bit : bits) {
			sum |= bit.bit;
		}
		return sum;
	}
}
