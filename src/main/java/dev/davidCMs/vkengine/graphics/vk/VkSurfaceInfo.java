package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.util.VkUtils;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;

public record VkSurfaceInfo(
        VkSurfaceCapabilities capabilities,
        Set<VkPresentMode> presentModes,
        Set<SurfaceFormat> formats
    ) {

    public record VkSurfaceCapabilities(
            Vector2i currentExtent,
            VkSurfaceTransform currentTransform,
            int maxArrayImageLayers,
            int maxImageCount,
            Vector2i maxImageExtent,
            int minImageCount,
            Vector2i minImageExtent,
            Set<VkCompositeAlpha> supportedCompositeAlpha,
            Set<VkSurfaceTransform> supportedTransforms,
            Set<VkImageUsage> supportedUsageFlags
        ) {

        public static VkSurfaceCapabilities getFrom(MemoryStack stack, VkPhysicalDevice physicalDevice, long surface) {
            VkSurfaceCapabilitiesKHR caps = VkSurfaceCapabilitiesKHR.malloc(stack);
            int err = KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice.getPhysicalDevice(), surface, caps);

            if (!VkUtils.successful(err))
                throw new RuntimeException("Could not query surface capabilities: " + VkUtils.translateErrorCode(err));

            return new VkSurfaceCapabilities(
                    VkUtils.extent2DToVector2i(caps.currentExtent()),
                    VkSurfaceTransform.valueOf(caps.currentTransform()),
                    caps.maxImageArrayLayers(),
                    caps.maxImageCount(),
                    VkUtils.extent2DToVector2i(caps.maxImageExtent()),
                    caps.minImageCount(),
                    VkUtils.extent2DToVector2i(caps.minImageExtent()),
                    VkCompositeAlpha.maskAsSet(caps.supportedCompositeAlpha()),
                    VkSurfaceTransform.maskAsSet(caps.supportedTransforms()),
                    VkImageUsage.maskAsSet(caps.supportedUsageFlags())
            );
        }
    }

    public record SurfaceFormat(VkImageColorSpace colorSpace, VkFormat format) {

        public static Set<SurfaceFormat> getFrom(MemoryStack stack, VkPhysicalDevice physicalDevice, long surface) {
            IntBuffer count = stack.callocInt(1);
            int err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.getPhysicalDevice(), surface, count, null);
            if (!VkUtils.successful(err))
                throw new RuntimeException("Could not query surface formats: " + VkUtils.translateErrorCode(err));
            VkSurfaceFormatKHR.Buffer buf = VkSurfaceFormatKHR.calloc(count.get(0), stack);
            err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.getPhysicalDevice(), surface, count, buf);
            if (!VkUtils.successful(err))
                throw new RuntimeException("Could not query surface formats: " + VkUtils.translateErrorCode(err));

            Set<SurfaceFormat> formats = new HashSet<>();
            for (int i = 0; i < count.get(0); i++) {
                formats.add(new SurfaceFormat(
                        VkImageColorSpace.valueOf(buf.get(i).colorSpace()),
                        VkFormat.valueOf(buf.get(i).format())
                ));
            }
            return formats;
        }
    }

    public static VkSurfaceInfo getFrom(VkPhysicalDevice physicalDevice, long surface) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            return new VkSurfaceInfo(
                    VkSurfaceCapabilities.getFrom(stack, physicalDevice, surface),
                    VkPresentMode.getFrom(stack, physicalDevice, surface),
                    SurfaceFormat.getFrom(stack, physicalDevice, surface)
            );
        }
    }

}
