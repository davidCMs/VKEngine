package dev.davidCMs.vkengine.common;

import org.joml.Vector2i;

public record Image(NativeByteBuffer data, Vector2i extent, int channels) {

    public void destroy() {
        data.close();
    }
}
