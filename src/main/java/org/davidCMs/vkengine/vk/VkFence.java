package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkFenceCreateInfo;

import java.nio.LongBuffer;
import java.util.List;
import java.util.Set;

public class VkFence {
    private final VkDeviceContext device;
    private final long fence;

    public VkFence(VkDeviceContext device) {
        this(device, null, 0);
    }

    public VkFence(VkDeviceContext device, boolean signaled) {
        int mask = signaled ? VkFenceCreateFlags.SIGNALED.bit : 0;
        this(device, null, mask);
    }

    public VkFence(VkDeviceContext device, PNextChainable pNext) {
        this(device, pNext, 0);
    }

    public VkFence(VkDeviceContext device, Set<VkFenceCreateFlags> flags) {
        this(device, null, VkFenceCreateFlags.getMaskOf(flags));
    }

    public VkFence(VkDeviceContext device, VkFenceCreateFlags... flags) {
        this(device, null, VkFenceCreateFlags.getMaskOf(flags));
    }

    public VkFence(VkDeviceContext device, PNextChainable pNext, Set<VkFenceCreateFlags> flags) {
        this(device, pNext, VkFenceCreateFlags.getMaskOf(flags));
    }

    public VkFence(VkDeviceContext device, PNextChainable pNext, VkFenceCreateFlags... flags) {
        this(device, pNext, VkFenceCreateFlags.getMaskOf(flags));
    }

    private VkFence(VkDeviceContext device, PNextChainable pNext, int flags) {
        this.device = device;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkFenceCreateInfo info = VkFenceCreateInfo.calloc(stack);
            info.sType$Default();
            if (pNext != null)
                info.pNext(pNext.getpNext(stack));
            info.flags(flags);

            LongBuffer lb = stack.mallocLong(1);

            int err;
            err = VK14.vkCreateFence(device.device(), info, null, lb);
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("Failed to create a fence: " + VkUtils.translateErrorCode(err));

            this.fence = lb.get(0);
        }
    }

    public static LongBuffer fencesToLB(MemoryStack stack, List<VkFence> fences) {
        LongBuffer lb = stack.mallocLong(fences.size());
        for (int i = 0; i < fences.size(); i++) {
            lb.put(i, fences.get(i).fence);
        }
        return lb;
    }

    public static LongBuffer fencesToLB(MemoryStack stack, VkFence[] fences) {
        LongBuffer lb = stack.mallocLong(fences.length);
        for (int i = 0; i < fences.length; i++) {
            lb.put(i, fences[i].fence);
        }
        return lb;
    }


    public void reset() {
        device.resetFences(this);
    }

    public void waitFor() {
        device.waitForFences(this);
    }

    public void waitFor(long timeout) {
        device.waitForFences(timeout, this);
    }

    long getFence() {
        return fence;
    }

    public VkDeviceContext getDevice() {
        return device;
    }
}
