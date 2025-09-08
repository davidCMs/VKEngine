package dev.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;

public interface VkClearValue {

	org.lwjgl.vulkan.VkClearValue toNativeVkClearValue(MemoryStack stack);

}
/*
	private final ColorRGBA color;
	private float depth;
	private int stencil;

	public VkClearValue(float r, float g, float b, float a, float depth, int stencil) {
		color = new ColorRGBA(r, g, b, a);

		ValueNotNormalizedException.check("depth", depth);

		this.depth = depth;
		this.stencil = stencil;
	}

	public VkClearValue(ColorRGBA color, float depth, int stencil) {
		this.color = color;

		ValueNotNormalizedException.check("depth", depth);

		this.depth = depth;
		this.stencil = stencil;
	}

	public VkClearValue(ColorRGBA color) {
		this.color = color;

		depth = 1;
		stencil = 0;
	}

	public VkClearValue(float depth, int stencil) {
		color = new ColorRGBA(0);

		this.depth = depth;
		this.stencil = stencil;
	}

	public ColorRGBA getColor() {
		return color;
	}

	public float getDepth() {
		return depth;
	}

	public void setDepth(float depth) {
		ValueNotNormalizedException.check("depth", depth);

		this.depth = depth;
	}

	public int getStencil() {
		return stencil;
	}

	public void setStencil(int stencil) {
		this.stencil = stencil;
	}

	public org.lwjgl.vulkan.VkClearValue toNativeVkClearValue(MemoryStack stack) {
		org.lwjgl.vulkan.VkClearValue val = org.lwjgl.vulkan.VkClearValue.calloc(stack);

		val.color(
				VkClearColorValue.calloc(stack)
						.float32(
								color.toFloatBuffer(stack)
						)
				);

		val.depthStencil(VkClearDepthStencilValue.calloc(stack)
				.set(
						depth,
						stencil
				)
		);

		return val;
	}

}

 */
