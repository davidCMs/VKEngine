package dev.davidCMs.vkengine.graphics.vma;

import dev.davidCMs.vkengine.common.BuilderSet;
import dev.davidCMs.vkengine.graphics.vk.VkMemoryPropertyFlags;
import dev.davidCMs.vkengine.util.ValueNotNormalizedException;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

public class VmaAllocationBuilder {

    private final static TaggedLogger log = Logger.tag("Vulkan");

    public static final VmaAllocationBuilder DEVICE = new Immutable(new VmaAllocationBuilder()
            .setUsage(VmaMemoryUsage.AUTO_PREFER_DEVICE)
            .preferredFlags().add(VkMemoryPropertyFlags.DEVICE_LOCAL).ret()
            .setPriority(1)
            .setMemoryTypeBits(-1));

    public static final VmaAllocationBuilder HOST = new Immutable(new VmaAllocationBuilder()
            .setUsage(VmaMemoryUsage.AUTO_PREFER_HOST)
            .requiredFlags().add(VkMemoryPropertyFlags.HOST_VISIBLE).ret()
            .preferredFlags().add(VkMemoryPropertyFlags.HOST_COHERENT).ret()
            .setPriority(1)
            .setMemoryTypeBits(-1)
            .flags.add(VmaAllocationCreateFlags.MAPPED_BIT).ret());

    public static final VmaAllocationBuilder AUTO = new Immutable(new VmaAllocationBuilder()
            .setUsage(VmaMemoryUsage.AUTO)
            .setPriority(1)
            .setMemoryTypeBits(-1));

    public static final VmaAllocationBuilder CPU_TO_GPU = new Immutable(new VmaAllocationBuilder()
            .setUsage(VmaMemoryUsage.CPU_TO_GPU)
            .setPriority(1)
            .setMemoryTypeBits(-1)
            .flags.add(VmaAllocationCreateFlags.MAPPED_BIT).ret());

    public static final VmaAllocationBuilder GPU_TO_CPU = new Immutable(new VmaAllocationBuilder()
            .setUsage(VmaMemoryUsage.GPU_TO_CPU)
            .setPriority(1)
            .setMemoryTypeBits(-1)
            .flags.add(VmaAllocationCreateFlags.MAPPED_BIT).ret());

    private final BuilderSet<VmaAllocationBuilder, VmaAllocationCreateFlags> flags = new BuilderSet<>(this);
    private final BuilderSet<VmaAllocationBuilder, VkMemoryPropertyFlags> requiredFlags = new BuilderSet<>(this);
    private final BuilderSet<VmaAllocationBuilder, VkMemoryPropertyFlags> preferredFlags = new BuilderSet<>(this);
    private VmaMemoryUsage usage;
    private int memoryTypeBits = -1;
    //private VmaPool; //todo implement
    private float priority = 0;

    public VmaAllocationCreateInfo build(MemoryStack stack) {
        VmaAllocationCreateInfo info = VmaAllocationCreateInfo.calloc(stack);
        info.flags((int) VmaAllocationCreateFlags.getMaskOf(flags));
        info.requiredFlags((int) VkMemoryPropertyFlags.getMaskOf(requiredFlags));
        info.preferredFlags((int) VkMemoryPropertyFlags.getMaskOf(preferredFlags));
        info.usage(usage.bit);
        info.memoryTypeBits(memoryTypeBits);
        info.priority(priority);

        return info;
    }

    public VmaMemoryUsage getUsage() {
        return usage;
    }

    public VmaAllocationBuilder setUsage(VmaMemoryUsage usage) {
        this.usage = usage;
        return this;
    }

    public int getMemoryTypeBits() {
        return memoryTypeBits;
    }

    public VmaAllocationBuilder setMemoryTypeBits(int memoryTypeBits) {
        this.memoryTypeBits = memoryTypeBits;
        return this;
    }

    public float getPriority() {
        return priority;
    }

    public VmaAllocationBuilder setPriority(float priority) {
        ValueNotNormalizedException.check(priority);
        this.priority = priority;
        return this;
    }

    public BuilderSet<VmaAllocationBuilder, VmaAllocationCreateFlags> flags() {
        return flags;
    }

    public BuilderSet<VmaAllocationBuilder, VkMemoryPropertyFlags> requiredFlags() {
        return requiredFlags;
    }

    public BuilderSet<VmaAllocationBuilder, VkMemoryPropertyFlags> preferredFlags() {
        return preferredFlags;
    }

    @Override
    public String toString() {
        return "VmaAllocationBuilder{" +
                "flags=" + flags +
                ", requiredFlags=" + requiredFlags +
                ", preferredFlags=" + preferredFlags +
                ", usage=" + usage +
                ", memoryTypeBits=" + memoryTypeBits +
                ", priority=" + priority +
                '}';
    }

    private static class Immutable extends VmaAllocationBuilder {
        private final VmaAllocationBuilder builder;

        public Immutable(VmaAllocationBuilder builder) {
            builder.flags.freeze();
            builder.requiredFlags.freeze();
            builder.preferredFlags.freeze();
            this.builder = builder;
        }

        @Override
        public VmaAllocationBuilder setUsage(VmaMemoryUsage usage) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public VmaAllocationBuilder setMemoryTypeBits(int memoryTypeBits) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public VmaAllocationBuilder setPriority(float priority) {
            throw new UnsupportedOperationException("");
        }

        @Override
        public VmaMemoryUsage getUsage() {
            return builder.getUsage();
        }

        @Override
        public VmaAllocationCreateInfo build(MemoryStack stack) {
            return builder.build(stack);
        }

        @Override
        public int getMemoryTypeBits() {
            return builder.getMemoryTypeBits();
        }

        @Override
        public float getPriority() {
            return builder.getPriority();
        }

        @Override
        public BuilderSet<VmaAllocationBuilder, VmaAllocationCreateFlags> flags() {
            return builder.flags();
        }

        @Override
        public BuilderSet<VmaAllocationBuilder, VkMemoryPropertyFlags> requiredFlags() {
            return builder.requiredFlags();
        }

        @Override
        public BuilderSet<VmaAllocationBuilder, VkMemoryPropertyFlags> preferredFlags() {
            return builder.preferredFlags();
        }
    }


}
