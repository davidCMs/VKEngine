package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.Copyable;
import org.davidCMs.vkengine.util.ValueNotNormalizedException;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

public class VkViewport implements Copyable {
	private final Vector2f pos;
	private final Vector2f size;
	private final Vector2f depth;

	public VkViewport() {
		pos = new Vector2f();
		size = new Vector2f();
		depth = new Vector2f(0, 1);
	}

	public VkViewport(Vector2f pos, Vector2f size, Vector2f depth) {
		this.pos = pos;
		this.size = size;

		ValueNotNormalizedException.check("depth.x", depth.x);
		ValueNotNormalizedException.check("depth.y", depth.y);

		this.depth = depth;
	}

	public VkViewport(VkViewport viewport) {
		this(new Vector2f(viewport.pos), new Vector2f(viewport.size), new Vector2f(viewport.depth));
	}

	public void setPos(float x, float y) {
		pos.set(x, y);
	}

	public void setX(float x) {
		pos.x = x;
	}

	public void setY(float y) {
		pos.y = y;
	}

	public Vector2f getPos() {
		return pos;
	}

	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y;
	}

	public void setSize(float width, float height) {
		size.set(width, height);
	}

	public void setWidth(float width) {
		size.x = width;
	}

	public void setHeight(float height) {
		size.y = height;
	}

	public Vector2f getSize() {
		return size;
	}

	public float getWidth() {
		return size.x;
	}

	public float getHeight() {
		return size.y;
	}

	public void setDepth(float min, float max) {
		setMinDepth(min);
		setMaxDepth(max);
	}

	public void setMinDepth(float min) {
		ValueNotNormalizedException.check("min", min);
		depth.x = min;
	}

	public void setMaxDepth(float max) {
		ValueNotNormalizedException.check("max", max);
		depth.y = max;
	}

	public Vector2f getDepth() {
		return depth;
	}

	public float getMinDepth() {
		return depth.x;
	}

	public float getMaxDepth() {
		return depth.y;
	}

	public org.lwjgl.vulkan.VkViewport toNative(MemoryStack stack) {
		return org.lwjgl.vulkan.VkViewport.calloc(stack)
				.x(pos.x)
				.y(pos.y)
				.width(size.x)
				.height(size.y)
				.minDepth(depth.x)
				.maxDepth(depth.y);
	}

	@Override
	public VkViewport copy() {
		return new VkViewport(this);
	}
}
