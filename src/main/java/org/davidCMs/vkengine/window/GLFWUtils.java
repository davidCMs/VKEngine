package org.davidCMs.vkengine.window;

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

    /** This is a utility method for converting form a java boolean to a GLFW boolean.
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

    /** This is a utility method for converting form a GLFW boolean to a java boolean.
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

}
