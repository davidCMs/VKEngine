package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.Copyable;
import org.joml.Vector2i;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkOffset2D;

public class VkRect2D implements Copyable {

	private final Vector2i offset;
	private final Vector2i extent;

	public VkRect2D() {
		offset = new Vector2i();
		extent = new Vector2i();
	}

	public VkRect2D(Vector2i offset, Vector2i extent) {
		this.offset = offset;
		this.extent = extent;
	}

	public VkRect2D(VkRect2D rect2D) {
		this(new Vector2i(rect2D.offset), new Vector2i(rect2D.extent));
	}

	public void setPos(int x, int y) {
		offset.set(x, y);
	}

	public void setX(int x) {
		offset.x = x;
	}

	public void setY(int y) {
		offset.y = y;
	}

	public Vector2i getOffset() {
		return offset;
	}

	public int getX() {
		return offset.x;
	}

	public int getY() {
		return offset.y;
	}

	public void setSize(int width, int height) {
		extent.set(width, height);
	}

	public void setWidth(int width) {
		extent.x = width;
	}

	public void setHeight(int height) {
		extent.y = height;
	}

	public Vector2i getExtent() {
		return extent;
	}

	public int getWidth() {
		return extent.x;
	}

	public int getHeight() {
		return extent.y;
	}

	public org.lwjgl.vulkan.VkRect2D toNative(MemoryStack stack) {
		return org.lwjgl.vulkan.VkRect2D.calloc(stack)
				.offset(VkOffset2D.calloc(stack)
						.set(offset.x, offset.y))
				.extent(VkExtent2D.calloc(stack)
						.set(extent.x, extent.y));
	}

	@Override
	public VkRect2D copy() {
		return new VkRect2D(this);
	}
}
