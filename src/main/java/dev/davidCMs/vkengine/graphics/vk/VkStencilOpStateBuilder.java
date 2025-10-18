package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkStencilOpState;

public class VkStencilOpStateBuilder implements Copyable {
	private VkStencilOp failOp;
	private VkStencilOp passOp;
	private VkStencilOp depthFailOp;
	private VkCompareOp compareOp;
	private int compareMask;
	private int writeMask;
	private int reference;

	public VkStencilOpState build(MemoryStack stack) {
		VkStencilOpState state = VkStencilOpState.calloc(stack);
		state.failOp(failOp.bit);
		state.passOp(passOp.bit);
		state.depthFailOp(depthFailOp.bit);
		state.compareOp(compareOp.bit);
		state.compareMask(compareMask);
		state.writeMask(writeMask);
		state.reference(reference);
		return state;
	}

	public VkStencilOp getFailOp() {
		return failOp;
	}

	public VkStencilOpStateBuilder setFailOp(VkStencilOp failOp) {
		this.failOp = failOp;
		return this;
	}

	public VkStencilOp getPassOp() {
		return passOp;
	}

	public VkStencilOpStateBuilder setPassOp(VkStencilOp passOp) {
		this.passOp = passOp;
		return this;
	}

	public VkStencilOp getDepthFailOp() {
		return depthFailOp;
	}

	public VkStencilOpStateBuilder setDepthFailOp(VkStencilOp depthFailOp) {
		this.depthFailOp = depthFailOp;
		return this;
	}

	public VkCompareOp getCompareOp() {
		return compareOp;
	}

	public VkStencilOpStateBuilder setCompareOp(VkCompareOp compareOp) {
		this.compareOp = compareOp;
		return this;
	}

	public int getCompareMask() {
		return compareMask;
	}

	public VkStencilOpStateBuilder setCompareMask(int compareMask) {
		this.compareMask = compareMask;
		return this;
	}

	public int getWriteMask() {
		return writeMask;
	}

	public VkStencilOpStateBuilder setWriteMask(int writeMask) {
		this.writeMask = writeMask;
		return this;
	}

	public int getReference() {
		return reference;
	}

	public VkStencilOpStateBuilder setReference(int reference) {
		this.reference = reference;
		return this;
	}

	@Override
	public VkStencilOpStateBuilder copy() {
		return new VkStencilOpStateBuilder()
				.setFailOp(failOp)
				.setPassOp(passOp)
				.setDepthFailOp(depthFailOp)
				.setCompareOp(compareOp)
				.setCompareMask(compareMask)
				.setWriteMask(writeMask)
				.setReference(reference);
	}
}
