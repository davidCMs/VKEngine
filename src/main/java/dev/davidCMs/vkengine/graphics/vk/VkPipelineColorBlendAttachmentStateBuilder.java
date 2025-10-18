package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;

import java.util.HashSet;
import java.util.Set;

public class VkPipelineColorBlendAttachmentStateBuilder implements Copyable {
	private Set<VkColorComponent> colorWriteMask = new HashSet<>();
	private boolean blendEnable = true;
	private VkBlendFactor srcColorBlendFactor = VkBlendFactor.ONE;
	private VkBlendFactor dstColorBlendFactor = VkBlendFactor.ZERO;
	private VkBlendOp colorBlendOp = VkBlendOp.ADD;
	private VkBlendFactor srcAlphaBlendFactor = VkBlendFactor.ONE;
	private VkBlendFactor dstAlphaBlendFactor = VkBlendFactor.ZERO;
	private VkBlendOp alphaBlendOp = VkBlendOp.ADD;

	public VkPipelineColorBlendAttachmentState build(MemoryStack stack) {
		if (colorWriteMask.isEmpty())
			colorWriteMask.addAll(Set.of(VkColorComponent.R, VkColorComponent.G, VkColorComponent.B, VkColorComponent.A));

		VkPipelineColorBlendAttachmentState info = VkPipelineColorBlendAttachmentState.calloc(stack);
		info.blendEnable(blendEnable);
		info.colorWriteMask((int) VkColorComponent.getMaskOf(colorWriteMask));
		info.srcColorBlendFactor(srcColorBlendFactor.bit);
		info.dstColorBlendFactor(dstColorBlendFactor.bit);
		info.colorBlendOp(colorBlendOp.bit);
		info.srcAlphaBlendFactor(srcAlphaBlendFactor.bit);
		info.dstAlphaBlendFactor(dstAlphaBlendFactor.bit);
		info.alphaBlendOp(alphaBlendOp.bit);
		return info;
	}

	public Set<VkColorComponent> getColorWriteMask() {
		return colorWriteMask;
	}

	public VkPipelineColorBlendAttachmentStateBuilder setColorWriteMask(Set<VkColorComponent> colorWriteMask) {
		this.colorWriteMask = colorWriteMask;
		return this;
	}

	public boolean isBlendEnable() {
		return blendEnable;
	}

	public VkPipelineColorBlendAttachmentStateBuilder setBlendEnable(boolean blendEnable) {
		this.blendEnable = blendEnable;
		return this;
	}

	public VkBlendFactor getSrcColorBlendFactor() {
		return srcColorBlendFactor;
	}

	public VkPipelineColorBlendAttachmentStateBuilder setSrcColorBlendFactor(VkBlendFactor srcColorBlendFactor) {
		this.srcColorBlendFactor = srcColorBlendFactor;
		return this;
	}

	public VkBlendFactor getDstColorBlendFactor() {
		return dstColorBlendFactor;
	}

	public VkPipelineColorBlendAttachmentStateBuilder setDstColorBlendFactor(VkBlendFactor dstColorBlendFactor) {
		this.dstColorBlendFactor = dstColorBlendFactor;
		return this;
	}

	public VkBlendOp getColorBlendOp() {
		return colorBlendOp;
	}

	public VkPipelineColorBlendAttachmentStateBuilder setColorBlendOp(VkBlendOp colorBlendOp) {
		this.colorBlendOp = colorBlendOp;
		return this;
	}

	public VkBlendFactor getSrcAlphaBlendFactor() {
		return srcAlphaBlendFactor;
	}

	public VkPipelineColorBlendAttachmentStateBuilder setSrcAlphaBlendFactor(VkBlendFactor srcAlphaBlendFactor) {
		this.srcAlphaBlendFactor = srcAlphaBlendFactor;
		return this;
	}

	public VkBlendFactor getDstAlphaBlendFactor() {
		return dstAlphaBlendFactor;
	}

	public VkPipelineColorBlendAttachmentStateBuilder setDstAlphaBlendFactor(VkBlendFactor dstAlphaBlendFactor) {
		this.dstAlphaBlendFactor = dstAlphaBlendFactor;
		return this;
	}

	public VkBlendOp getAlphaBlendOp() {
		return alphaBlendOp;
	}

	public VkPipelineColorBlendAttachmentStateBuilder setAlphaBlendOp(VkBlendOp alphaBlendOp) {
		this.alphaBlendOp = alphaBlendOp;
		return this;
	}

	@Override
	public VkPipelineColorBlendAttachmentStateBuilder copy() {
		return new VkPipelineColorBlendAttachmentStateBuilder()
				.setBlendEnable(blendEnable)
				.setSrcColorBlendFactor(srcColorBlendFactor)
				.setDstColorBlendFactor(dstColorBlendFactor)
				.setColorBlendOp(colorBlendOp)
				.setSrcAlphaBlendFactor(srcAlphaBlendFactor)
				.setDstAlphaBlendFactor(dstAlphaBlendFactor)
				.setAlphaBlendOp(alphaBlendOp);
	}
}
