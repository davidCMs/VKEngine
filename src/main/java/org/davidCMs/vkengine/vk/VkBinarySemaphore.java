package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

public class VkBinarySemaphore extends VkSemaphore {
    private final long semaphore;

    public VkBinarySemaphore(VkDeviceContext device) {
        this(device, null);
    }

    public VkBinarySemaphore(VkDeviceContext device, PNextChainable pNext) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSemaphoreCreateInfo info = VkSemaphoreCreateInfo.calloc(stack);
            info.sType$Default();
            if (pNext != null)
                info.pNext(pNext.getpNext(stack));

            LongBuffer lb = stack.mallocLong(1);

            int err;
            err = VK14.vkCreateSemaphore(device.device(), info, null, lb);
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("Failed to create a binary semaphore: " + VkUtils.translateErrorCode(err));

            this.semaphore = lb.get(0);
        }
    }

    long getSemaphore() {
        return semaphore;
    }
}
