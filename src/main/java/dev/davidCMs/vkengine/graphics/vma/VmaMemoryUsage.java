package dev.davidCMs.vkengine.graphics.vma;

import java.util.Collection;

import static org.lwjgl.util.vma.Vma.*;

public enum VmaMemoryUsage {

    AUTO(VMA_MEMORY_USAGE_AUTO),
    AUTO_PREFER_DEVICE(VMA_MEMORY_USAGE_AUTO_PREFER_DEVICE),
    AUTO_PREFER_HOST(VMA_MEMORY_USAGE_AUTO_PREFER_HOST),
    CPU_COPY(VMA_MEMORY_USAGE_CPU_COPY),
    CPU_ONLY(VMA_MEMORY_USAGE_CPU_ONLY),
    CPU_TO_GPU(VMA_MEMORY_USAGE_CPU_TO_GPU),
    GPU_LAZILY_ALLOCATED(VMA_MEMORY_USAGE_GPU_LAZILY_ALLOCATED),
    GPU_ONLY(VMA_MEMORY_USAGE_GPU_ONLY),
    GPU_TO_CPU(VMA_MEMORY_USAGE_GPU_TO_CPU),
    UNKNOWN(VMA_MEMORY_USAGE_UNKNOWN)

    ;

    final int bit;

    VmaMemoryUsage(int bit) {
        this.bit = bit;
    }

}
