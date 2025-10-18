package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;

import static org.lwjgl.vulkan.VK14.*;

public enum VkVertexInputRate {

    INSTANCE(VK_VERTEX_INPUT_RATE_INSTANCE),
    VERTEX(VK_VERTEX_INPUT_RATE_VERTEX)

    ;

    final int bit;

    VkVertexInputRate(int bit) {
        this.bit = bit;
    }

}
