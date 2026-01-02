package dev.davidCMs.vkengine.util;

import dev.davidCMs.vkengine.common.Image;
import dev.davidCMs.vkengine.common.NativeByteBuffer;
import org.joml.Vector2i;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ImageUtils {
    public static Image decodeImg(NativeByteBuffer byteBuffer, int channels) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer image = STBImage.stbi_load_from_memory(byteBuffer.asReadOnlyBuffer(), width, height, comp, channels);

            if (image == null) throw new RuntimeException("Failed to decode image");

            return new Image(NativeByteBuffer.wrap(image), new Vector2i(width.get(0), height.get(0)), channels);
        }
    }
}
