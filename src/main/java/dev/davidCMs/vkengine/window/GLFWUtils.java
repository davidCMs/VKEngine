package dev.davidCMs.vkengine.window;

import dev.davidCMs.vkengine.graphics.vk.VkExtension;
import dev.davidCMs.vkengine.graphics.vk.VkInstanceBuilder;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryUtil;

import java.util.HashSet;
import java.util.Set;


/** The {@code GLFWUtils} class is a utility class containing static methods that are useful when dealing with GLFW BS
 *
 * @author davidCMs
 * @since 0.0.1
 * */
public class GLFWUtils {

    /** A utility method for converting form a java boolean to a GLFW boolean.
     *
     * @param bool The java boolean that will be converted
     * @return The GLFW enum representation of the converted java boolean
     *
     * @since 0.0.1
     * */
    public static int ToGLFWBool(boolean bool) {
        if (bool)
            return GLFW.GLFW_TRUE;
        else
            return GLFW.GLFW_FALSE;
    }

    /** A utility method for converting form a GLFW boolean to a java boolean.
     *
     * @param bool The GLFW boolean that will be converted
     * @return The java boolean converted from an GLFW enum representation of a boolean
     *
     * @since 0.0.1
     * */
    public static boolean fromGLFWBool(int bool) {
        switch (bool) {
            case GLFW.GLFW_TRUE -> {
                return true;
            }
            case GLFW.GLFW_FALSE -> {
                return false;
            }
            default -> throw new IllegalArgumentException("Unknown value: \"" + bool + "\"");
        }
    }

    /** A utility method for getting all the required vulkan extensions for rendering to GLFW windows
     *
     * @return returns a set of {@link VkExtension} that need to be enabled in the {@link VkInstanceBuilder} to render to the GLFW window
     *
     * @since 0.0.1
     * */
    public static Set<VkExtension> getRequiredVkExtensions() {
        if (!GLFWVulkan.glfwVulkanSupported())
            throw new IllegalStateException("This system does not support vulkan.");

        PointerBuffer extensionsPtr = GLFWVulkan.glfwGetRequiredInstanceExtensions();
        if (extensionsPtr == null)
            throw new IllegalStateException("Failed to get extensions required for GLFW.");

        Set<VkExtension> extensionNames = new HashSet<>(extensionsPtr.remaining());

        for (int i = 0; i <extensionsPtr.remaining(); i++) {
            long addr = extensionsPtr.get(i);
            extensionNames.add(VkExtension.of(MemoryUtil.memUTF8Safe(addr)));
        }

        return extensionNames;
    }

}
