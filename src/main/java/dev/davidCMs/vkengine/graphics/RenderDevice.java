package dev.davidCMs.vkengine.graphics;

import dev.davidCMs.vkengine.common.Fence;
import dev.davidCMs.vkengine.common.IFence;
import dev.davidCMs.vkengine.graphics.vk.*;
import dev.davidCMs.vkengine.graphics.vma.VmaAllocationBuilder;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public class RenderDevice {

    private final VkDeviceContext device;
    private final RenderDeviceResourceManager resourceManager;
    private final List<VkQueue> queues;

    private final VkQueue graphicsQueue;
    private final VkQueue computeQueue;
    private final List<VkQueue> transferQueues;

    private final boolean useComputeForTransfer;
    private final AtomicInteger nextTransferQueueIndex = new AtomicInteger();

    public static VkPhysicalDevice pickBestDevice(VkInstanceContext instance, long surface) {
        HashMap<VkPhysicalDevice, Integer> score = new HashMap<>();
        Set<VkPhysicalDevice> usable = new HashSet<>();

        for (VkPhysicalDevice physicalDevice : VkPhysicalDevice.getAvailablePhysicalDevices(instance)) {
            VkPhysicalDeviceInfo deviceInfo = physicalDevice.getInfo();

            if (!VkPhysicalDeviceExtensionUtils.checkAvailabilityOf(physicalDevice, VkPhysicalDeviceExtensionUtils.VK_KHR_SWAPCHAIN))
                continue;

            boolean canPresent = false;
            for (VkQueueFamily family : VkQueueFamily.getDeviceQueueFamilies(physicalDevice)) {
                if (family.canRenderTo(surface)) {
                    canPresent = true;
                    break;
                }
            }
            if (!canPresent) continue;

            usable.add(physicalDevice);
            score.put(physicalDevice,
                    physicalDevice.getInfo().properties().deviceType() == VkPhysicalDeviceInfo.VkPhysicalDeviceProperties.VkPhysicalDeviceType.DISCRETE ?
                    1000 : 0);

        }

        if (usable.size() == 1) return usable.iterator().next();

        for (VkPhysicalDevice physicalDevice : score.keySet()) {
            int s = score.get(physicalDevice);
            s += physicalDevice.getInfo().queueFamilies().size() * 10;

            VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.VkMemoryHeap heap = null;
            for (int i = 0; i < physicalDevice.getInfo().memoryProperties().getMemoryHeapCount(); i++) {
                VkPhysicalDeviceInfo.VkPhysicalDeviceMemoryProperties.VkMemoryHeap h = physicalDevice.getInfo().memoryProperties().getMemoryHeap(i);
                boolean deviceLocal = VkMemoryPropertyFlags.doesMaskHave(h.flags(), VkMemoryPropertyFlags.DEVICE_LOCAL);
                if (!deviceLocal) continue;
                if (heap == null) {
                    heap = h;
                    continue;
                }
                if (h.size() > heap.size()) heap = h;
            }

            s += (int) ((float) heap.size() / 1024.0f / 1024.0f / 1024.0f * 10f);
            score.put(physicalDevice, s);
        }

        Map.Entry<VkPhysicalDevice, Integer> best = null;
        for (Map.Entry<VkPhysicalDevice, Integer> entry : score.entrySet()) {
            if (best == null) {
                best = entry;
                continue;
            }
            if (entry.getValue() > best.getValue()) best = entry;
        }

        if (best == null) throw new RuntimeException("Failed to find sutable device");

        return best.getKey();
    }

    public RenderDevice(VkPhysicalDevice physicalDevice, VkPhysicalDeviceFeaturesBuilder features, Iterable<String> extensions) {
        this(physicalDevice, features, StreamSupport.stream(extensions.spliterator(), false).toArray(String[]::new));
    }

    public RenderDevice(VkPhysicalDevice physicalDevice, VkPhysicalDeviceFeaturesBuilder features, String... extensions) {
        Set<VkQueueFamily> queueFamilies = physicalDevice.getInfo().queueFamilies();

        VkQueueFamily graphics = null;
        VkQueueFamily compute = null;
        Set<VkQueueFamily> transfers = new HashSet<>();
        Set<VkQueueFamily> rest = new HashSet<>();

        for (VkQueueFamily family : queueFamilies) {
            boolean unused = true;
            if (family.capableOfGraphics() && graphics == null) {
                graphics = family;
                unused = false;
            }
            if (family.capableOfCompute() && !family.capableOfGraphics() && compute == null) {
                compute = family;
                unused = false;
            }
            if (family.capableOfTransfer() && !family.capableOfGraphics() && ! family.capableOfCompute()) {
                transfers.add(family);
                unused = false;
            }
            if (unused)
                rest.add(family);
        }

        if (graphics == null) throw new RuntimeException("Provided physical device does not support graphics");
        if (compute == null && graphics.capableOfCompute())
            compute = graphics;

        boolean usesComputeForTransfer = false;
        if (transfers.isEmpty()) {
            transfers.add(compute);
            usesComputeForTransfer = true;
        }

        this.useComputeForTransfer = usesComputeForTransfer;

        Set<VkDeviceBuilderQueueInfo> queueInfos = new HashSet<>();
        queueInfos.add(graphics.makeCreateInfo());
        if (graphics != compute) queueInfos.add(compute.makeCreateInfo());

        if (!usesComputeForTransfer)
            for (VkQueueFamily queue : transfers)
                queueInfos.add(queue.makeCreateInfo());

        for (VkQueueFamily queueFamily : rest) {
            queueInfos.add(queueFamily.makeCreateInfo());
        }

        this.device = new VkDeviceBuilder()
                .setQueueInfos(queueInfos)
                .setPhysicalDevice(physicalDevice)
                .setExtensions(extensions)
                .setpNext(features)
                .build();

        HashMap<VkQueueFamily, VkQueue[]> map = device.queueMap();

        this.graphicsQueue = map.get(graphics)[0];
        this.computeQueue = map.get(compute)[0];

        Set<VkQueue> transferQueues = new HashSet<>();
        for (VkQueueFamily transfer : transfers)
            transferQueues.add(map.get(transfer)[0]);

        this.transferQueues = transferQueues.stream().toList();

        Set<VkQueue> queues = new HashSet<>();
        for (Map.Entry<VkQueueFamily, VkQueue[]> queue : map.entrySet()) {
            queues.add(queue.getValue()[0]);
        }

        this.queues = queues.stream().toList();

        this.resourceManager = new RenderDeviceResourceManager(device, transferQueues);

    }

    public VkQueue getPresentQueue(long surface) {
        for (VkQueue queue : queues) {
            if (queue.getQueueFamily().canRenderTo(surface)) return queue;
        }
        return null;
    }

    public IFence uploadAsync(VkBuffer buf, ByteBuffer data) {
        VkBuffer buffer = new VkBufferBuilder()
                .setAllocationBuilder(VmaAllocationBuilder.HOST)
                .getUsage().add(
                        VkBufferUsageFlags.TRANSFER_SRC
                ).ret()
                .setSize(buf.getSize())
                .build(device);
        buffer.writeData(data);


        IFence fence = new Fence();
        resourceManager.submit(fence, buffer::destroy, (pool) -> {
            VkCommandBuffer cmd = pool.get().createCommandBuffer()
                    .begin(VkCommandBufferUsageFlags.ONE_TIME_SUBMIT)
                    .copyBuffer(buffer, buf)
                    .end();

            VkQueue.VkSubmitInfoBuilder[] builders = new VkQueue.VkSubmitInfoBuilder[1];
            builders[0] = new VkQueue.VkSubmitInfoBuilder().setCommandBuffers(cmd);
            return builders;
        });
        return fence;
    }

    public void destroy() {
        resourceManager.destroy();
        device.destroy();
    }

    public VkDeviceContext getDevice() {
        return device;
    }

    public List<VkQueue> getQueues() {
        return queues;
    }

    public VkQueue getGraphicsQueue() {
        return graphicsQueue;
    }

    public VkQueue getComputeQueue() {
        return computeQueue;
    }

    public List<VkQueue> getTransferQueues() {
        return transferQueues;
    }

    public boolean usesComputeForTransfer() {
        return useComputeForTransfer;
    }

    public VkQueue getTransferQueue() {
        return transferQueues.getFirst();
    }

}
