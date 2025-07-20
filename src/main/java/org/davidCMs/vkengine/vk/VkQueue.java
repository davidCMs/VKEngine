package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.List;
import java.util.concurrent.Semaphore;

public class VkQueue {

    private static final Logger log = LogManager.getLogger(VkQueue.class);
    private final org.lwjgl.vulkan.VkQueue queue;
    private final VkQueueFamily queueFamily;

    VkQueue(org.lwjgl.vulkan.VkQueue queue, VkQueueFamily queueFamily) {
        this.queue = queue;
        this.queueFamily = queueFamily;
    }

    public static class VkSubmitInfoBuilder {

        public static class VkSemaphoreSubmitInfo {
            private final VkSemaphore semaphore;
            private final long value;
            private final List<VkPipelineStage> stageMask;

            public VkSemaphoreSubmitInfo(VkBinarySemaphore semaphore, List<VkPipelineStage> stageMask) {
                this.semaphore = semaphore;
                this.value = 0;
                this.stageMask = stageMask;
            }

            public VkSemaphoreSubmitInfo(VkBinarySemaphore semaphore, VkPipelineStage... stageMask) {
                this.semaphore = semaphore;
                this.value = 0;
                this.stageMask = List.of(stageMask);
            }

            private org.lwjgl.vulkan.VkSemaphoreSubmitInfo build(MemoryStack stack) {
                org.lwjgl.vulkan.VkSemaphoreSubmitInfo info = org.lwjgl.vulkan.VkSemaphoreSubmitInfo.calloc(stack);
                info.sType$Default();
                info.semaphore(semaphore.getSemaphore());
                info.stageMask(VkPipelineStage.getMaskOf(stageMask));
                info.value(value);
                return info;
            }

            //todo implement constructors for timeline semaphores when there implemented
        }

        private List<VkSemaphoreSubmitInfo> waitSemaphoreInfos;
        private List<VkCommandBuffer> commandBuffers;
        private List<VkSemaphoreSubmitInfo> signalSemaphoreInfos;

        public VkSubmitInfoBuilder setWaitSemaphores(List<VkSemaphoreSubmitInfo> waitSemaphore) {
            this.waitSemaphoreInfos = waitSemaphore;
            return this;
        }

        public VkSubmitInfoBuilder setWaitSemaphores(VkSemaphoreSubmitInfo waitSemaphore) {
            this.waitSemaphoreInfos = List.of(waitSemaphore);
            return this;
        }

        public VkSubmitInfoBuilder setCommandBuffers(List<VkCommandBuffer> commandBuffers) {
            this.commandBuffers = commandBuffers;
            return this;
        }

        public VkSubmitInfoBuilder setCommandBuffers(VkCommandBuffer... commandBuffers) {
            this.commandBuffers = List.of(commandBuffers);
            return this;
        }

        public VkSubmitInfoBuilder setSignalSemaphores(List<VkSemaphoreSubmitInfo> signalSemaphore) {
            this.signalSemaphoreInfos = signalSemaphore;
            return this;
        }

        public VkSubmitInfoBuilder setSignalSemaphores(VkSemaphoreSubmitInfo signalSemaphore) {
            this.signalSemaphoreInfos = List.of(signalSemaphore);
            return this;
        }

        public List<VkSemaphoreSubmitInfo> getWaitSemaphores() {
            return waitSemaphoreInfos;
        }

        public List<VkCommandBuffer> getCommandBuffers() {
            return commandBuffers;
        }

        public List<VkSemaphoreSubmitInfo> getSignalSemaphores() {
            return signalSemaphoreInfos;
        }

        private org.lwjgl.vulkan.VkSemaphoreSubmitInfo.Buffer semaphoresToBuffer(MemoryStack stack, List<VkSemaphoreSubmitInfo> semaphoreSubmitInfos) {
            org.lwjgl.vulkan.VkSemaphoreSubmitInfo.Buffer buf = org.lwjgl.vulkan.VkSemaphoreSubmitInfo.calloc(semaphoreSubmitInfos.size(), stack);
            for (int i = 0; i < semaphoreSubmitInfos.size(); i++) {
                buf.put(i, semaphoreSubmitInfos.get(i).build(stack));
            }
            return buf;
        }

        private VkCommandBufferSubmitInfo.Buffer commandBuffersToBuffer(MemoryStack stack) {
            VkCommandBufferSubmitInfo.Buffer buf = VkCommandBufferSubmitInfo.calloc(commandBuffers.size(), stack);
            for (int i = 0; i < commandBuffers.size(); i++) {
                VkCommandBufferSubmitInfo info = buf.get(i);
                info.sType$Default();
                info.commandBuffer(commandBuffers.get(i).getCommandBuffer());
                info.deviceMask(0x1);
            }
            return buf;
        }

        private VkSubmitInfo2 build(MemoryStack stack) {
            VkSubmitInfo2 submitInfo = VkSubmitInfo2.calloc(stack);
            submitInfo.sType$Default();
            if (waitSemaphoreInfos != null)
                submitInfo.pWaitSemaphoreInfos(semaphoresToBuffer(stack, waitSemaphoreInfos));
            if (commandBuffers != null)
                submitInfo.pCommandBufferInfos(commandBuffersToBuffer(stack));
            if (signalSemaphoreInfos != null)
                submitInfo.pSignalSemaphoreInfos(semaphoresToBuffer(stack, signalSemaphoreInfos));
            return submitInfo;
        }

        public static VkSubmitInfo2.Buffer submitBuildersToBuffer(MemoryStack stack, List<VkSubmitInfoBuilder> builders) {
            VkSubmitInfo2.Buffer buf = VkSubmitInfo2.calloc(builders.size(), stack);
            for (int i = 0; i < builders.size(); i++) {
                buf.put(i, builders.get(i).build(stack));
            }
            return buf;
        }

        public static VkSubmitInfo2.Buffer submitBuildersToBuffer(MemoryStack stack, VkSubmitInfoBuilder[] builders) {
            VkSubmitInfo2.Buffer buf = VkSubmitInfo2.calloc(builders.length, stack);
            for (int i = 0; i < builders.length; i++) {
                buf.put(i, builders[i].build(stack));
            }
            return buf;
        }

    }

    public void submit(VkSubmitInfoBuilder... submitBuilders) {
        submit(null, submitBuilders);
    }

    public void submit(List<VkSubmitInfoBuilder> submitBuilders) {
        submit(null, submitBuilders);
    }

    public void submit(VkFence fence, List<VkSubmitInfoBuilder> submitBuilders) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VK14.vkQueueSubmit2(
                    queue,
                    VkSubmitInfoBuilder.submitBuildersToBuffer(stack, submitBuilders),
                    fence != null ? fence.getFence() : VK14.VK_NULL_HANDLE
            );
        }
    }

    public void submit(VkFence fence, VkSubmitInfoBuilder... submitBuilders) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VK14.vkQueueSubmit2(
                    queue,
                    VkSubmitInfoBuilder.submitBuildersToBuffer(stack, submitBuilders),
                    fence != null ? fence.getFence() : VK14.VK_NULL_HANDLE
            );
        }
    }

    public void present(VkSemaphore semaphore, VkSwapchainContext swapchain, int imageIndex) {
        present(
                new VkSemaphore[]{semaphore},
                new VkSwapchainContext[]{swapchain},
                new int[]{imageIndex}
        );
    }

    public void present(VkSemaphore[] semaphores, VkSwapchainContext[] swapchains, int[] imageIndices) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            LongBuffer semaphoresLB = stack.mallocLong(semaphores.length);
            for (int i = 0; i < semaphores.length; i++) {
                semaphoresLB.put(i, semaphores[i].getSemaphore());
            }

            LongBuffer swapchainsLB = stack.mallocLong(swapchains.length);
            for (int i = 0; i < swapchains.length; i++) {
                swapchainsLB.put(i, swapchains[i].getSwapchain());
            }

            IntBuffer imageIndicesLB = stack.ints(imageIndices);


            VkPresentInfoKHR info = VkPresentInfoKHR.calloc(stack);
            info.sType$Default();
            info.pWaitSemaphores(semaphoresLB);
            info.pSwapchains(swapchainsLB);
            info.swapchainCount(swapchains.length);
            info.pImageIndices(imageIndicesLB);

            int err;
            err = KHRSwapchain.vkQueuePresentKHR(queue, info);
            if (err != VK14.VK_SUCCESS)
                if (VkUtils.successful(err))
                    log.warn("Warning while presenting image: " + VkUtils.translateErrorCode(err));
                else throw new RuntimeException("Error while presenting image: " + VkUtils.translateErrorCode(err));
        }

    }

    public VkQueueFamily getQueueFamily() {
        return queueFamily;
    }
}
