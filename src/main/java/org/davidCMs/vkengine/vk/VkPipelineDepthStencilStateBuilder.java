package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;

public class VkPipelineDepthStencilStateBuilder implements Copyable {
	private boolean depthTestEnable;
	private boolean depthWriteEnable;
	private VkCompareOp depthCompareOp;
	private boolean depthBoundsTestEnable;
	private boolean stencilTestEnable;
	private VkStencilOpStateBuilder front;
	private VkStencilOpStateBuilder back;
	private float minDepthBounds;
	private float maxDepthBounds;

	public VkPipelineDepthStencilStateCreateInfo build(MemoryStack stack) {
		VkPipelineDepthStencilStateCreateInfo info = VkPipelineDepthStencilStateCreateInfo.calloc(stack);
		info.sType$Default();
		info.depthTestEnable(depthTestEnable);
		info.depthWriteEnable(depthWriteEnable);
		info.depthCompareOp(depthCompareOp.bit);
		info.depthBoundsTestEnable(depthBoundsTestEnable);
		info.stencilTestEnable(stencilTestEnable);
		info.front(front.build(stack));
		info.back(back.build(stack));
		info.minDepthBounds(minDepthBounds);
		info.maxDepthBounds(maxDepthBounds);
		return info;
	}

	public boolean isDepthTestEnable() {
		return depthTestEnable;
	}

	public VkPipelineDepthStencilStateBuilder setDepthTestEnable(boolean depthTestEnable) {
		this.depthTestEnable = depthTestEnable;
		return this;
	}

	public boolean isDepthWriteEnable() {
		return depthWriteEnable;
	}

	public VkPipelineDepthStencilStateBuilder setDepthWriteEnable(boolean depthWriteEnable) {
		this.depthWriteEnable = depthWriteEnable;
		return this;
	}

	public VkCompareOp getDepthCompareOp() {
		return depthCompareOp;
	}

	public VkPipelineDepthStencilStateBuilder setDepthCompareOp(VkCompareOp depthCompareOp) {
		this.depthCompareOp = depthCompareOp;
		return this;
	}

	public boolean isDepthBoundsTestEnable() {
		return depthBoundsTestEnable;
	}

	public VkPipelineDepthStencilStateBuilder setDepthBoundsTestEnable(boolean depthBoundsTestEnable) {
		this.depthBoundsTestEnable = depthBoundsTestEnable;
		return this;
	}

	public boolean isStencilTestEnable() {
		return stencilTestEnable;
	}

	public VkPipelineDepthStencilStateBuilder setStencilTestEnable(boolean stencilTestEnable) {
		this.stencilTestEnable = stencilTestEnable;
		return this;
	}

	public VkStencilOpStateBuilder getFront() {
		return front;
	}

	public VkPipelineDepthStencilStateBuilder setFront(VkStencilOpStateBuilder front) {
		this.front = front;
		return this;
	}

	public VkStencilOpStateBuilder getBack() {
		return back;
	}

	public VkPipelineDepthStencilStateBuilder setBack(VkStencilOpStateBuilder back) {
		this.back = back;
		return this;
	}

	public float getMinDepthBounds() {
		return minDepthBounds;
	}

	public VkPipelineDepthStencilStateBuilder setMinDepthBounds(float minDepthBounds) {
		this.minDepthBounds = minDepthBounds;
		return this;
	}

	public float getMaxDepthBounds() {
		return maxDepthBounds;
	}

	public VkPipelineDepthStencilStateBuilder setMaxDepthBounds(float maxDepthBounds) {
		this.maxDepthBounds = maxDepthBounds;
		return this;
	}

	public VkPipelineDepthStencilStateBuilder copy() {
		return new VkPipelineDepthStencilStateBuilder()
				.setDepthTestEnable(depthTestEnable)
				.setDepthWriteEnable(depthWriteEnable)
				.setDepthCompareOp(depthCompareOp)
				.setDepthBoundsTestEnable(depthBoundsTestEnable)
				.setStencilTestEnable(stencilTestEnable)
				.setFront(front.copy())
				.setBack(back.copy())
				.setMinDepthBounds(minDepthBounds)
				.setMaxDepthBounds(maxDepthBounds)
				;
	}


}
