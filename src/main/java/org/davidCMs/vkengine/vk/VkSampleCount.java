package org.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkSampleCount {

	SAMPLE_1(VK_SAMPLE_COUNT_1_BIT),
	SAMPLE_2(VK_SAMPLE_COUNT_2_BIT),
	SAMPLE_4(VK_SAMPLE_COUNT_4_BIT),
	SAMPLE_8(VK_SAMPLE_COUNT_8_BIT),
	SAMPLE_16(VK_SAMPLE_COUNT_16_BIT),
	SAMPLE_32(VK_SAMPLE_COUNT_32_BIT),
	SAMPLE_64(VK_SAMPLE_COUNT_64_BIT)

	;

	final int bit;

	VkSampleCount(int bit) {
		this.bit = bit;
	}
}
