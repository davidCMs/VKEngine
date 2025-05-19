package org.davidCMs.vkengine.util;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Collection;

public class BufUtil {

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

}
