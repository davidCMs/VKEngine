package dev.davidCMs.vkengine.graphics.vk;

import org.tinylog.TaggedLogger;
import dev.davidCMs.vkengine.util.VkUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Collection;
import java.util.List;

public class VkQueue {

    private static final TaggedLogger log = org.tinylog.Logger.tag("Vulkan");
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

        public static VkSubmitInfo2.Buffer submitBuildersToBuffer(MemoryStack stack, Collection<VkSubmitInfoBuilder> builders) {
            VkSubmitInfo2.Buffer buf = VkSubmitInfo2.calloc(builders.size(), stack);
            int i = 0;
            for (VkSubmitInfoBuilder builder : builders) {
                buf.put(i++, builder.build(stack));
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

    public void submit(Collection<VkSubmitInfoBuilder> submitBuilders) {
        submit(null, submitBuilders);
    }

    public void submit(@Nullable VkFence fence, Collection<VkSubmitInfoBuilder> submitBuilders) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VK14.vkQueueSubmit2(
                    queue,
                    VkSubmitInfoBuilder.submitBuildersToBuffer(stack, submitBuilders),
                    fence != null ? fence.getFence() : VK14.VK_NULL_HANDLE
            );
        }
    }

    public void submit(@Nullable VkFence fence, VkSubmitInfoBuilder... submitBuilders) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VK14.vkQueueSubmit2(
                    queue,
                    VkSubmitInfoBuilder.submitBuildersToBuffer(stack, submitBuilders),
                    fence != null ? fence.getFence() : VK14.VK_NULL_HANDLE
            );
        }
    }

    public void present(VkSemaphore semaphore, VkSwapchainContext swapchain, int imageIndex) {
        present(null, semaphore, swapchain, imageIndex);
    }

    public void present(VkFence fence, VkSemaphore semaphore, VkSwapchainContext swapchain, int imageIndex) {
        present(
                fence != null ? new VkFence[]{fence} : null,
                new VkSemaphore[]{semaphore},
                new VkSwapchainContext[]{swapchain},
                new int[]{imageIndex}
        );
    }

    public void present(VkFence[] fences, VkSemaphore[] semaphores, VkSwapchainContext[] swapchains, int[] imageIndices) {
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

            if (fences != null && fences.length != 0) {
                LongBuffer fencesLB = stack.mallocLong(fences.length);
                for (int i = 0; i < fences.length; i++)
                    fencesLB.put(i, fences[i].getFence());

                info.pNext(VkSwapchainPresentFenceInfoEXT.calloc(stack)
                        .sType$Default()
                        .swapchainCount(swapchains.length)
                        .pFences(fencesLB));
            }

            int err;
            err = KHRSwapchain.vkQueuePresentKHR(queue, info);
            if (err != VK14.VK_SUCCESS)
                if (VkUtils.successful(err))
                    log.warn("Warning while presenting image: " + VkUtils.translateErrorCode(err));
                else throw new RuntimeException("Error while presenting image: " + VkUtils.translateErrorCode(err));
        }

    }

    public void present(VkSemaphore semaphore, VkSwapchain swapchain, int imageIndex) {
        present(null, semaphore, swapchain, imageIndex);
    }

    public void present(VkFence fence, VkSemaphore semaphore, VkSwapchain swapchain, int imageIndex) {
        present(
                fence != null ? new VkFence[]{fence} : null,
                new VkSemaphore[]{semaphore},
                new VkSwapchain[]{swapchain},
                new int[]{imageIndex}
        );
    }

    public void present(VkFence[] fences, VkSemaphore[] semaphores, VkSwapchain[] swapchains, int[] imageIndices) {
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

            if (fences != null && fences.length != 0) {
                LongBuffer fencesLB = stack.mallocLong(fences.length);
                for (int i = 0; i < fences.length; i++)
                    fencesLB.put(i, fences[i].getFence());

                info.pNext(VkSwapchainPresentFenceInfoEXT.calloc(stack)
                        .sType$Default()
                        .swapchainCount(swapchains.length)
                        .pFences(fencesLB));
            }

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
