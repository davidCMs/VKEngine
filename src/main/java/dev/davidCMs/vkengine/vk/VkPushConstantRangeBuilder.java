package dev.davidCMs.vkengine.vk;

import dev.davidCMs.vkengine.shader.ShaderStage;
import dev.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPushConstantRange;

import java.util.HashSet;
import java.util.Set;

public class VkPushConstantRangeBuilder implements Copyable {
	private Set<ShaderStage> stageFlags;
	private int offset;
	private int size;

	public VkPushConstantRange build(MemoryStack stack) {
		VkPushConstantRange range = VkPushConstantRange.calloc(stack);
		range.stageFlags(ShaderStage.getVkMaskOf(stageFlags));
		range.offset(offset);
		range.size(size);
		return range;
	}

	public Set<ShaderStage> getStageFlags() {
		return stageFlags;
	}

	public VkPushConstantRangeBuilder setStageFlags(Set<ShaderStage> stageFlags) {
		this.stageFlags = stageFlags;
		return this;
	}

	public int getOffset() {
		return offset;
	}

	public VkPushConstantRangeBuilder setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public int getSize() {
		return size;
	}

	public VkPushConstantRangeBuilder setSize(int size) {
		this.size = size;
		return this;
	}

	@Override
	public Copyable copy() {
		return new VkPushConstantRangeBuilder()
				.setStageFlags(stageFlags != null ? new HashSet<>(stageFlags) : null)
				.setOffset(offset)
				.setSize(size);
	}
}
