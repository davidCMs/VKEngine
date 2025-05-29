package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.EXTDebugUtils;

import java.util.Collection;

public enum VkDebugMessageType {

	PERFORMANCE(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT),
	VALIDATION(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT),
	GENERAL(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT)

	;

	final int bit;

	VkDebugMessageType(int bit) {
		this.bit = bit;
	}

	static int getMaskOf(VkDebugMessageType... bits) {
		int sum = 0;
		for (VkDebugMessageType bit : bits) {
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getMaskOf(Collection<VkDebugMessageType> debugMessageTypes) {
		return getMaskOf(debugMessageTypes.toArray(debugMessageTypes.toArray(new VkDebugMessageType[0])));
	}
}
