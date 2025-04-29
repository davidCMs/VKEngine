package org.davidCMs.vkengine.window;

import org.lwjgl.glfw.GLFW;

public class GlfwEnums {
    public enum CursorState {

        GLFW_CURSOR_NORMAL(GLFW.GLFW_CURSOR_NORMAL),
        GLFW_CURSOR_HIDDEN(GLFW.GLFW_CURSOR_HIDDEN),
        GLFW_CURSOR_DISABLED(GLFW.GLFW_CURSOR_DISABLED),
        GLFW_CURSOR_CAPTURED(GLFW.GLFW_CURSOR_CAPTURED);

        private final int constant;

        CursorState(int constant) {
            this.constant = constant;
        }

        public static CursorState fromConstant(int constant) {
            for (CursorState state : CursorState.values()) {
                if (state.constant == constant) {
                    return state;
                }
            }
            throw new GLFWException("Unknown constant: " + constant);
        }

        public int getConstant() {
            return constant;
        }
    }
}
