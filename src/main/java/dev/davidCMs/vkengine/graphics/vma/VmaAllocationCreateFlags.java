package dev.davidCMs.vkengine.graphics.vma;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.util.vma.Vma.*;

public enum VmaAllocationCreateFlags {


    CAN_ALIAS_BIT(VMA_ALLOCATION_CREATE_CAN_ALIAS_BIT),
    DEDICATED_MEMORY_BIT (VMA_ALLOCATION_CREATE_DEDICATED_MEMORY_BIT ),
    DONT_BIND_BIT(VMA_ALLOCATION_CREATE_DONT_BIND_BIT),
    HOST_ACCESS_ALLOW_TRANSFER_INSTEAD_BIT(VMA_ALLOCATION_CREATE_HOST_ACCESS_ALLOW_TRANSFER_INSTEAD_BIT),
    HOST_ACCESS_RANDOM_BIT(VMA_ALLOCATION_CREATE_HOST_ACCESS_RANDOM_BIT),
    HOST_ACCESS_SEQUENTIAL_WRITE_BIT(VMA_ALLOCATION_CREATE_HOST_ACCESS_SEQUENTIAL_WRITE_BIT),
    MAPPED_BIT(VMA_ALLOCATION_CREATE_MAPPED_BIT),
    NEVER_ALLOCATE_BIT(VMA_ALLOCATION_CREATE_NEVER_ALLOCATE_BIT),
    STRATEGY_BEST_FIT_BIT(VMA_ALLOCATION_CREATE_STRATEGY_BEST_FIT_BIT),
    STRATEGY_MASK(VMA_ALLOCATION_CREATE_STRATEGY_MASK),
    STRATEGY_MIN_MEMORY_BIT(VMA_ALLOCATION_CREATE_STRATEGY_MIN_MEMORY_BIT),
    STRATEGY_MIN_OFFSET_BIT(VMA_ALLOCATION_CREATE_STRATEGY_MIN_OFFSET_BIT),
    STRATEGY_MIN_TIME_BIT(VMA_ALLOCATION_CREATE_STRATEGY_MIN_TIME_BIT),
    UPPER_ADDRESS_BIT(VMA_ALLOCATION_CREATE_UPPER_ADDRESS_BIT),
    USER_DATA_COPY_STRING_BIT(VMA_ALLOCATION_CREATE_USER_DATA_COPY_STRING_BIT),
    WITHIN_BUDGET_BIT(VMA_ALLOCATION_CREATE_WITHIN_BUDGET_BIT),

    ;

    final int bit;

    VmaAllocationCreateFlags(int bit) {
        this.bit = bit;
    }

    public static long getMaskOf(VmaAllocationCreateFlags... bits) {
    	if (bits == null) return 0;

    	long sum = 0;
    	for (VmaAllocationCreateFlags bit : bits) {
    		if (bit == null) continue;
    		sum |= bit.bit;
    	}
    	return sum;
    }

    public static long getMaskOf(Iterable<VmaAllocationCreateFlags> bits) {
        if (bits == null) return 0;

        long sum = 0;
        for (VmaAllocationCreateFlags bit : bits) {
            if (bit == null) continue;
            sum |= bit.bit;
        }
        return sum;
    }

    public static boolean doesMaskHave(long mask, VmaAllocationCreateFlags bit) {
        if (bit == null) return false;
        return (mask & bit.bit) != 0;
    }

    public static Set<VmaAllocationCreateFlags> maskAsSet(long mask) {
        Set<VmaAllocationCreateFlags> set = new HashSet<>();
        for (VmaAllocationCreateFlags bit : values()) {
            if ((bit.bit & mask) != 0) set.add(bit);
        }
        return set;
    }

    public static VmaAllocationCreateFlags valueOf(long bitVal) {
        for (VmaAllocationCreateFlags bit : values()) {
            if (bit.bit == bitVal) return bit;
        }
        throw new IllegalArgumentException("No Value for bit: " + bitVal);
    }

}
