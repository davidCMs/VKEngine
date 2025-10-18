package dev.davidCMs.vkengine.graphics.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkPolygonMode {

	FILL(VK_POLYGON_MODE_FILL),
	LINE(VK_POLYGON_MODE_LINE),
	POINT(VK_POLYGON_MODE_POINT)

	;

	final int bit;

	VkPolygonMode(int bit) {
		this.bit = bit;
	}
}
