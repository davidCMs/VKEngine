package org.davidCMs.vkengine.vk;

import java.util.Set;

public record VkBuffer(long buffer, Set<VkBufferUsageFlags> usage) {
}
