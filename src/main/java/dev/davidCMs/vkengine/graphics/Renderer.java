package dev.davidCMs.vkengine.graphics;

import dev.davidCMs.vkengine.common.Destroyable;
import dev.davidCMs.vkengine.graphics.vk.VkCommandBuffer;
import dev.davidCMs.vkengine.graphics.vk.VkCommandPool;
import dev.davidCMs.vkengine.graphics.vk.VkImage;
import dev.davidCMs.vkengine.graphics.vk.VkImageView;
import org.joml.Vector2i;

public abstract class Renderer implements Destroyable {

    protected final RenderDevice renderDevice;

    public Renderer(RenderDevice renderDevice) {
        this.renderDevice = renderDevice;
    }

    abstract void updateRenderArea(Vector2i newArea);
    abstract void render(VkImageView image, VkCommandBuffer buffer);

}
