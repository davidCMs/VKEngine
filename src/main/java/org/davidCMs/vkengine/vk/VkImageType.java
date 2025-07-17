package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;

public enum VkImageType {

	TYPE_1D(VK14.VK_IMAGE_TYPE_1D),
	TYPE_2D(VK14.VK_IMAGE_TYPE_2D),
	TYPE_3D(VK14.VK_IMAGE_TYPE_3D),
	TYPE_CUBE(VK14.VK_IMAGE_VIEW_TYPE_CUBE),
	TYPE_1D_ARRAY(VK14.VK_IMAGE_VIEW_TYPE_1D_ARRAY),
	TYPE_2D_ARRAY(VK14.VK_IMAGE_VIEW_TYPE_2D_ARRAY),
	TYPE_CUBE_ARRAY(VK14.VK_IMAGE_VIEW_TYPE_CUBE_ARRAY);

	;

	final int bit;

	VkImageType(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}
}
