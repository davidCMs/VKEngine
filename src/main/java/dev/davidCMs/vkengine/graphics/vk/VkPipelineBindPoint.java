package dev.davidCMs.vkengine.graphics.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkPipelineBindPoint {

	COMPUTE(VK_PIPELINE_BIND_POINT_COMPUTE),
	GRAPHICS(VK_PIPELINE_BIND_POINT_GRAPHICS)

	;

	final int bit;

	VkPipelineBindPoint(int bit) {
		this.bit = bit;
	}
}
