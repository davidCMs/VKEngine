package org.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkAttachmentLoadOp {

	LOAD(VK_ATTACHMENT_LOAD_OP_LOAD),
	CLEAR(VK_ATTACHMENT_LOAD_OP_CLEAR),
	NONE(VK_ATTACHMENT_LOAD_OP_NONE),
	DONT_CARE(VK_ATTACHMENT_LOAD_OP_DONT_CARE)

	;

	final int bit;

	VkAttachmentLoadOp(int bit) {
		this.bit = bit;
	}
}
