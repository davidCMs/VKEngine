package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.BuilderSet;

public class VkDeviceExtensionInfo {

    private final BuilderSet<VkDeviceExtensionInfo, VkDeviceExtension> wantedExtensions = new BuilderSet<>(this);
    private final BuilderSet<VkDeviceExtensionInfo, VkDeviceExtension> requiredExtension = new BuilderSet<>(this);

    public BuilderSet<VkDeviceExtensionInfo, VkDeviceExtension> wantedExtensions() {
        return wantedExtensions;
    }

    public BuilderSet<VkDeviceExtensionInfo, VkDeviceExtension> requiredExtension() {
        return requiredExtension;
    }
}
