package org.davidCMs.vkengine.util;

import org.joml.Vector2i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkExtent2D;

import java.nio.ByteBuffer;
import java.util.Collection;

public class VkUtil {

	public static PointerBuffer stringsToPointerBuffer(MemoryStack stack, String... strings) {
		PointerBuffer ptr = stack.callocPointer(strings.length);

		for (String name : strings) {
			ByteBuffer buf = stack.UTF8(name);
			ptr.put(buf);
		}

		ptr.flip();
		return ptr;
	}

	public static PointerBuffer stringsToPointerBuffer(MemoryStack stack, Collection<String> strings) {
		return stringsToPointerBuffer(stack, strings.toArray(strings.toArray(new String[0])));
	}

	public static VkExtent2D Vector2iToExtent2D(Vector2i vec, MemoryStack stack) {
		return VkExtent2D.calloc(stack).set(vec.x, vec.y);
	}

}
