package org.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkAttachmentStoreOp {

	NONE(VK_ATTACHMENT_STORE_OP_NONE),
	DONT_CARE(VK_ATTACHMENT_STORE_OP_DONT_CARE),
	STORE(VK_ATTACHMENT_STORE_OP_STORE)

	;

	final int bit;

	VkAttachmentStoreOp(int bit) {
		this.bit = bit;
	}
}
