package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;

public class VkPipelineRasterizationStateBuilder implements Copyable {
	private boolean depthClampEnable = false;
	private boolean rasterizerDiscardEnable = false;
	private VkPolygonMode polygonMode = VkPolygonMode.FILL;
	private float lineWidth = 1.0f;
	private VkCullMode cullMode = VkCullMode.BACK;
	private VkFrontFace frontFace = VkFrontFace.COUNTER_CLOCKWISE;
	private boolean depthBiasEnable = false;
	private float depthBiasConstantFactor = 0.0f;
	private float depthBiasClamp = 0.0f;
	private float depthBiasSlopeFactor = 0.0f;

	public VkPipelineRasterizationStateCreateInfo build(MemoryStack stack) {
		VkPipelineRasterizationStateCreateInfo info = VkPipelineRasterizationStateCreateInfo.calloc(stack);
		info.sType$Default();
		info.depthClampEnable(depthClampEnable);
		info.rasterizerDiscardEnable(rasterizerDiscardEnable);
		info.polygonMode(polygonMode.bit);
		info.lineWidth(lineWidth);
		info.cullMode(cullMode.bit);
		info.frontFace(frontFace.bit);
		info.depthBiasEnable(depthBiasEnable);
		info.depthBiasConstantFactor(depthBiasConstantFactor);
		info.depthBiasClamp(depthBiasClamp);
		info.depthBiasSlopeFactor(depthBiasSlopeFactor);
		return info;
	}

	public boolean isDepthClampEnable() {
		return depthClampEnable;
	}

	public VkPipelineRasterizationStateBuilder setDepthClampEnable(boolean depthClampEnable) {
		this.depthClampEnable = depthClampEnable;
		return this;
	}

	public boolean isRasterizerDiscardEnable() {
		return rasterizerDiscardEnable;
	}

	public VkPipelineRasterizationStateBuilder setRasterizerDiscardEnable(boolean rasterizerDiscardEnable) {
		this.rasterizerDiscardEnable = rasterizerDiscardEnable;
		return this;
	}

	public VkPolygonMode getPolygonMode() {
		return polygonMode;
	}

	public VkPipelineRasterizationStateBuilder setPolygonMode(VkPolygonMode polygonMode) {
		this.polygonMode = polygonMode;
		return this;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public VkPipelineRasterizationStateBuilder setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
		return this;
	}

	public VkCullMode getCullMode() {
		return cullMode;
	}

	public VkPipelineRasterizationStateBuilder setCullMode(VkCullMode cullMode) {
		this.cullMode = cullMode;
		return this;
	}

	public VkFrontFace getFrontFace() {
		return frontFace;
	}

	public VkPipelineRasterizationStateBuilder setFrontFace(VkFrontFace frontFace) {
		this.frontFace = frontFace;
		return this;
	}

	public boolean isDepthBiasEnable() {
		return depthBiasEnable;
	}

	public VkPipelineRasterizationStateBuilder setDepthBiasEnable(boolean depthBiasEnable) {
		this.depthBiasEnable = depthBiasEnable;
		return this;
	}

	public float getDepthBiasConstantFactor() {
		return depthBiasConstantFactor;
	}

	public VkPipelineRasterizationStateBuilder setDepthBiasConstantFactor(float depthBiasConstantFactor) {
		this.depthBiasConstantFactor = depthBiasConstantFactor;
		return this;
	}

	public float getDepthBiasClamp() {
		return depthBiasClamp;
	}

	public VkPipelineRasterizationStateBuilder setDepthBiasClamp(float depthBiasClamp) {
		this.depthBiasClamp = depthBiasClamp;
		return this;
	}

	public float getDepthBiasSlopeFactor() {
		return depthBiasSlopeFactor;
	}

	public VkPipelineRasterizationStateBuilder setDepthBiasSlopeFactor(float depthBiasSlopeFactor) {
		this.depthBiasSlopeFactor = depthBiasSlopeFactor;
		return this;
	}

	public VkPipelineRasterizationStateBuilder copy() {
		return new VkPipelineRasterizationStateBuilder()
				.setDepthClampEnable(depthClampEnable)
				.setRasterizerDiscardEnable(rasterizerDiscardEnable)
				.setPolygonMode(polygonMode)
				.setLineWidth(lineWidth)
				.setCullMode(cullMode)
				.setFrontFace(frontFace)
				.setDepthBiasEnable(depthBiasEnable)
				.setDepthBiasClamp(depthBiasClamp)
				.setDepthBiasConstantFactor(depthBiasConstantFactor);
	}
}
