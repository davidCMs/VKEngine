package org.davidCMs.vkengine.common;

import org.davidCMs.vkengine.util.ValueNotNormalizedException;
import org.davidCMs.vkengine.vk.VkClearValue;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkClearColorValue;

import java.nio.FloatBuffer;

public class ColorRGBA implements VkClearValue {

	private float r;
	private float g;
	private float b;
	private float a;

	public ColorRGBA(float r, float g, float b, float a) {
		r(r);
		g(g);
		b(b);
		a(a);
	}

	public ColorRGBA(float s) {
		this(s, s, s, s);
	}

	public ColorRGBA(ColorRGBA color) {
		this(color.r, color.g, color.b, color.a);
	}

	public float r() {
		return r;
	}

	public void r(float r) {
		ValueNotNormalizedException.check("r", r);
		this.r = r;
	}

	public float g() {
		return g;
	}

	public void g(float g) {
		ValueNotNormalizedException.check("g", g);
		this.g = g;
	}

	public float b() {
		return b;
	}

	public void b(float b) {
		ValueNotNormalizedException.check("b", b);
		this.b = b;
	}

	public float a() {
		return a;
	}

	public void a(float a) {
		ValueNotNormalizedException.check("a", a);
		this.a = a;
	}

	public float[] toArray() {
		return new float[]{r, g, b, a};
	}

	public FloatBuffer toFloatBuffer(MemoryStack stack) {
		return stack.floats(toArray());
	}


	@Override
	public org.lwjgl.vulkan.VkClearValue toNativeVkClearValue(MemoryStack stack) {
		org.lwjgl.vulkan.VkClearValue val = org.lwjgl.vulkan.VkClearValue.calloc(stack);
		val.color(
				VkClearColorValue.calloc(stack)
						.float32(
								toFloatBuffer(stack)
						)
		);
		return val;
	}
}
