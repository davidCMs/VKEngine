package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.ColorRGBA;
import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;

import java.util.List;

public class VkPipelineColorBlendStateBuilder implements Copyable {
	private boolean logicOpEnable = false;
	private VkLogicOp logicOp = VkLogicOp.NO_OP;
	private List<VkPipelineColorBlendAttachmentStateBuilder> blendAttachments;
	private ColorRGBA blendConstants = new ColorRGBA(1);

	private VkPipelineColorBlendAttachmentState.Buffer getAttachmentsBuffer(MemoryStack stack) {
		VkPipelineColorBlendAttachmentState.Buffer buf = VkPipelineColorBlendAttachmentState.calloc(blendAttachments.size(), stack);
		for (int i = 0; i < blendAttachments.size(); i++) {
			buf.put(i, blendAttachments.get(i).build(stack));
		}
		return buf;
	}

	public VkPipelineColorBlendStateCreateInfo build (MemoryStack stack) {
		VkPipelineColorBlendStateCreateInfo info = VkPipelineColorBlendStateCreateInfo.calloc(stack);
		info.sType$Default();
		info.logicOpEnable(logicOpEnable);
		info.logicOp(logicOp.bit);
		info.attachmentCount(blendAttachments.size());
		info.pAttachments(getAttachmentsBuffer(stack));
		info.blendConstants(blendConstants.toFloatBuffer(stack));
		return info;
	}

	public boolean isLogicOpEnable() {

		return logicOpEnable;
	}

	public VkPipelineColorBlendStateBuilder setLogicOpEnable(boolean logicOpEnable) {
		this.logicOpEnable = logicOpEnable;
		return this;
	}

	public VkLogicOp getLogicOp() {
		return logicOp;
	}

	public VkPipelineColorBlendStateBuilder setLogicOp(VkLogicOp logicOp) {
		this.logicOp = logicOp;
		return this;
	}

	public List<VkPipelineColorBlendAttachmentStateBuilder> getBlendAttachments() {
		return blendAttachments;
	}

	public VkPipelineColorBlendStateBuilder setBlendAttachments(List<VkPipelineColorBlendAttachmentStateBuilder> blendAttachments) {
		this.blendAttachments = blendAttachments;
		return this;
	}

	public ColorRGBA blendConstants() {
		return blendConstants;
	}

	public VkPipelineColorBlendStateBuilder setBlendConstants(ColorRGBA blendConstants) {
		this.blendConstants = blendConstants;
		return this;
	}

	public VkPipelineColorBlendStateBuilder copy() {
		return new VkPipelineColorBlendStateBuilder()
				.setLogicOpEnable(logicOpEnable)
				.setLogicOp(logicOp)
				.setBlendAttachments(Copyable.copyList(blendAttachments))
				.setBlendConstants(new ColorRGBA(blendConstants));
	}
}
