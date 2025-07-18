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

    private static LongBuffer fencesToLB(MemoryStack stack, List<VkFence> fences) {
        LongBuffer lb = stack.mallocLong(fences.size());
        for (int i = 0; i < fences.size(); i++) {
            lb.put(i, fences.get(i).fence);
        }
        return lb;
    }

    private static LongBuffer fencesToLB(MemoryStack stack, VkFence[] fences) {
        LongBuffer lb = stack.mallocLong(fences.length);
        for (int i = 0; i < fences.length; i++) {
            lb.put(i, fences[i].fence);
        }
        return lb;
    }

    public void reset() {
        resetFences(this);
    }

    public static void resetFences(List<VkFence> fences) {
        if (fences == null || fences.isEmpty())
            return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VK14.vkResetFences(
                    fences.getFirst().device.device(),
                    fencesToLB(stack, fences)
            );
        }
    }

    public static void resetFences(VkFence... fences) {
        if (fences == null || fences.length < 1)
            return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VK14.vkResetFences(
                    fences[0].device.device(),
                    fencesToLB(stack, fences)
            );
        }
    }

    public void waitFor() {
        waitForFences(this);
    }

    public void waitFor(long timeout) {
        waitForFences(timeout, this);
    }

    public static void waitForFences(List<VkFence> fences) {
        waitForFences(-1, true, fences);
    }

    public static void waitForFences(VkFence... fences) {
        waitForFences(-1, true, fences);
    }

    public static void waitForFences(boolean waitAll, List<VkFence> fences) {
        waitForFences(-1, waitAll, fences);
    }

    public static void waitForFences(boolean waitAll, VkFence... fences) {
        waitForFences(-1, waitAll, fences);
    }

    public static void waitForFences(long timeout, List<VkFence> fences) {
        waitForFences(timeout, true, fences);
    }

    public static void waitForFences(long timeout, VkFence... fences) {
        waitForFences(timeout, true, fences);
    }

    public static void waitForFences(long timeout, boolean waitAll, List<VkFence> fences) {
        if (fences == null || fences.isEmpty())
            return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VK14.vkWaitForFences(
                    fences.getFirst().device.device(),
                    fencesToLB(stack, fences),
                    waitAll,
                    timeout
            );
        }
    }

    public static void waitForFences(long timeout, boolean waitAll, VkFence... fences) {
        if (fences == null || fences.length < 1)
            return;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VK14.vkWaitForFences(
                    fences[0].device.device(),
                    fencesToLB(stack, fences),
                    waitAll,
                    timeout
            );
        }
    }

    long getFence() {
        return fence;
    }

    public VkDeviceContext getDevice() {
        return device;
    }
}
