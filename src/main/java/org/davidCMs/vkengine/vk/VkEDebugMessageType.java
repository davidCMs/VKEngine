package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.EXTDebugUtils;

import java.util.Collection;
import java.util.Set;

public enum VkEDebugMessageType {

	PERFORMANCE(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT),
	VALIDATION(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT),
	GENERAL(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT)

	;

	final int bit;

	VkEDebugMessageType(int bit) {
		this.bit = bit;
	}

	static int getValueOf(VkEDebugMessageType... bits) {
		int sum = 0;
		for (VkEDebugMessageType bit : bits) {
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getValueOf(Collection<VkEDebugMessageType> debugMessageTypes) {
		return getValueOf(debugMessageTypes.toArray(debugMessageTypes.toArray(new VkEDebugMessageType[0])));
	}
}
