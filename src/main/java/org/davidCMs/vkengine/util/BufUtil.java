package org.davidCMs.vkengine.util;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BufUtil {

	public static PointerBuffer stringsToPointerBuffer(String... strings) {
		PointerBuffer ptr = MemoryUtil.memAllocPointer(strings.length);

		for (String name : strings) {
			ByteBuffer buf = MemoryUtil.memUTF8(name);
			ptr.put(buf);
		}

		ptr.flip();
		return ptr;
	}

}
