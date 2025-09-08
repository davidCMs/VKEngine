package dev.davidCMs.vkengine.window;

import static org.lwjgl.glfw.GLFW.*;

public class GlfwEnums {
    public enum CursorState {

        CURSOR_NORMAL(GLFW_CURSOR_NORMAL),
        CURSOR_HIDDEN(GLFW_CURSOR_HIDDEN),
        CURSOR_DISABLED(GLFW_CURSOR_DISABLED),
        CURSOR_CAPTURED(GLFW_CURSOR_CAPTURED);

        final int constant;

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

    }

    public enum MouseButton {

        LEFT(GLFW_MOUSE_BUTTON_LEFT),
        RIGHT(GLFW_MOUSE_BUTTON_RIGHT),
        MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE),
        MB1(GLFW_MOUSE_BUTTON_1),
        MB2(GLFW_MOUSE_BUTTON_2),
        MB3(GLFW_MOUSE_BUTTON_3),
        MB4(GLFW_MOUSE_BUTTON_4),
        MB5(GLFW_MOUSE_BUTTON_5),
        MB6(GLFW_MOUSE_BUTTON_6),
        MB7(GLFW_MOUSE_BUTTON_7),
        MB8(GLFW_MOUSE_BUTTON_8),

        ;

        final int constant;

        MouseButton(int constant) {
            this.constant = constant;
        }

        public static MouseButton fromConstant(int constant) {
            for (MouseButton buttons : MouseButton.values()) {
                if (buttons.constant == constant) {
                    return buttons;
                }
            }
            throw new GLFWException("Unknown constant: " + constant);
        }

    }

    public enum MouseButtonState {

        PRESSED(GLFW_PRESS),
        RELEASED(GLFW_RELEASE)

        ;

        final int constant;

        MouseButtonState(int constant) {
            this.constant = constant;
        }

        public static MouseButtonState fromConstant(int constant) {
            for (MouseButtonState buttons : MouseButtonState.values()) {
                if (buttons.constant == constant) {
                    return buttons;
                }
            }
            throw new GLFWException("Unknown constant: " + constant);
        }

        public boolean isPressed() {
            return this == PRESSED;
        }

    }

}
