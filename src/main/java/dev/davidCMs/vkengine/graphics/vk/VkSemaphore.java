package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.Destroyable;

public abstract class VkSemaphore implements Destroyable {

    abstract long getSemaphore();

}
