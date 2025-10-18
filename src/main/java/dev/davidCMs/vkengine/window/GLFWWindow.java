package dev.davidCMs.vkengine.window;

import dev.davidCMs.vkengine.graphics.vk.VkInstanceContext;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstance;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static dev.davidCMs.vkengine.window.GLFWUtils.ToGLFWBool;
import static dev.davidCMs.vkengine.window.GLFWUtils.fromGLFWBool;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The {@code GLFWWindow} class is an encapsulation of a GLFW window which provides methods for manipulating the
 * encapsulated window without needing to use any GLFW functions.
 *
 * @author davidCMs
 * @since 0.0.1
 */
public class GLFWWindow implements AutoCloseable {

    /** The handle to the GLFW window.
     * @since 0.0.1
     */
    private final long window;

    private final HashMap<VkInstance, Long> surfaces = new HashMap<>();

    private final List<AutoCloseable> autoCloseableCbs = new ArrayList<>();

    /** List of {@code GLFWKeyCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWKeyCallbackI> keyCallbacks = new ArrayList<>();

    /** List of {@code GLFWCharCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWCharCallbackI> charCallbacks = new ArrayList<>();

    /** List of {@code GLFWDropCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWDropCallbackI> dropCallbacks = new ArrayList<>();

    /** List of {@code GLFWPreeditCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWPreeditCallbackI> preeditCallbacks = new ArrayList<>();

    /** List of {@code GLFWScrollCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWScrollCallbackI> scrollCallbacks = new ArrayList<>();

    /** List of {@code GLFWCharModsCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWCharModsCallbackI> charModsCallbacks = new ArrayList<>();

    /** List of {@code GLFWCursorEnterCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWCursorEnterCallbackI> cursorEnterCallbacks = new ArrayList<>();

    /** List of {@code GLFWCursorPosCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWCursorPosCallbackI> cursorPosCallbacks = new ArrayList<>();

    /** List of {@code GLFWFramebufferSizeCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWFramebufferSizeCallbackI> framebufferSizeCallbacks = new ArrayList<>();

    /** List of {@code GLFWMouseButtonCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWMouseButtonCallbackI> mouseButtonCallbacks = new ArrayList<>();

    /** List of {@code GLFWWindowCloseCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowCloseCallbackI> windowCloseCallbacks = new ArrayList<>();

    /** List of {@code GLFWWindowFocusCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowFocusCallbackI> windowFocusCallbacks = new ArrayList<>();

    /** List of {@code GLFWWindowIconifyCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowIconifyCallbackI> windowIconifyCallbacks = new ArrayList<>();

    /** List of {@code GLFWWindowMaximizeCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowMaximizeCallbackI> windowMaximizeCallbacks = new ArrayList<>();

    /** List of {@code GLFWWindowPosCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowPosCallbackI> windowPosCallbacks = new ArrayList<>();

    /** List of {@code GLFWWindowRefreshCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowRefreshCallbackI> windowRefreshCallbacks = new ArrayList<>();

    /** List of {@code GLFWWindowSizeCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowSizeCallbackI> windowSizeCallbacks = new ArrayList<>();

    /** List of {@code GLFWWindowContentScaleCallbackI} that get called by the window.
     * @since 0.0.1
     */
    private final List<GLFWWindowContentScaleCallbackI> windowContentScaleCallbacks = new ArrayList<>();

    /** the total amount of mouse wheel scroll that was done in the window */
    private float totalScroll = 0;

    /** Main constructor responsible for creating the window.
     *
     * @param width The width of the window to be created.
     * @param height The height of the window to be created.
     * @param title The title of the window to be created.
     *
     * @since 0.0.1
     */
    public GLFWWindow(int width, int height, String title) {

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CLIENT_API, GLFW_FALSE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        setupCallbacks();

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }
    }

    /** Sets up internal callback so that all the callbacks in the callback arrays get called. */
    private void setupCallbacks() {
        GLFWKeyCallback kcb = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                for (GLFWKeyCallbackI cb : keyCallbacks) {
                    cb.invoke(window, key, scancode, action, mods);
                }
            }
        };
        glfwSetKeyCallback(window, kcb);

        GLFWCharCallback ccb = new GLFWCharCallback() {
            @Override
            public void invoke(long window, int codepoint) {
                for (GLFWCharCallbackI cb : charCallbacks)
                    cb.invoke(window, codepoint);
            }
        };
        glfwSetCharCallback(window, ccb);

        GLFWDropCallback dcb = new GLFWDropCallback() {
            @Override
            public void invoke(long window, int count, long names) {
                for (GLFWDropCallbackI cb : dropCallbacks)
                    cb.invoke(window, count, names);
            }
        };
        glfwSetDropCallback(window, dcb);

        GLFWPreeditCallback pcb = new GLFWPreeditCallback() {
            @Override
            public void invoke(long window, int preedit_count, long preedit_string, int block_count, long block_sizes, int focused_block, int caret) {
                for (GLFWPreeditCallbackI cb : preeditCallbacks)
                    cb.invoke(window, preedit_count, preedit_string, block_count, block_sizes, focused_block, caret);
            }
        };
        glfwSetPreeditCallback(window, pcb);

        GLFWScrollCallback scb = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                for (GLFWScrollCallbackI cb : scrollCallbacks)
                    cb.invoke(window, xoffset, yoffset);
                totalScroll += (float) yoffset;
            }
        };
        glfwSetScrollCallback(window, scb);

        GLFWCharModsCallback cmcb = new GLFWCharModsCallback() {
            @Override
            public void invoke(long window, int codepoint, int mods) {
                for (GLFWCharModsCallbackI cb : charModsCallbacks)
                    cb.invoke(window, codepoint, mods);
            }
        };
        glfwSetCharModsCallback(window, cmcb);

        GLFWCursorEnterCallback cecb = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                for (GLFWCursorEnterCallbackI cb : cursorEnterCallbacks)
                    cb.invoke(window, entered);
            }
        };
        glfwSetCursorEnterCallback(window, cecb);

        GLFWCursorPosCallback cpcb = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                for (GLFWCursorPosCallbackI cb : cursorPosCallbacks)
                    cb.invoke(window, xpos, ypos);
            }
        };
        glfwSetCursorPosCallback(window, cpcb);

        GLFWFramebufferSizeCallback fscb = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                for (GLFWFramebufferSizeCallbackI cb : framebufferSizeCallbacks)
                    cb.invoke(window, width, height);
            }
        };
        glfwSetFramebufferSizeCallback(window, fscb);

        GLFWMouseButtonCallback mbcb = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                for (GLFWMouseButtonCallbackI cb : mouseButtonCallbacks)
                    cb.invoke(window, button, action, mods);
            }
        };
        glfwSetMouseButtonCallback(window, mbcb);

        GLFWWindowCloseCallback wccb = new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                for (GLFWWindowCloseCallbackI cb : windowCloseCallbacks)
                    cb.invoke(window);
            }
        };
        glfwSetWindowCloseCallback(window, wccb);

        GLFWWindowFocusCallback wfcb = new GLFWWindowFocusCallback() {
            @Override
            public void invoke(long window, boolean focused) {
                for (GLFWWindowFocusCallbackI cb : windowFocusCallbacks)
                    cb.invoke(window, focused);
            }
        };
        glfwSetWindowFocusCallback(window, wfcb);

        GLFWWindowIconifyCallback wicb = new GLFWWindowIconifyCallback() {
            @Override
            public void invoke(long window, boolean iconified) {
                for (GLFWWindowIconifyCallbackI cb : windowIconifyCallbacks)
                    cb.invoke(window, iconified);
            }
        };
        glfwSetWindowIconifyCallback(window, wicb);

        GLFWWindowMaximizeCallback wmcb = new GLFWWindowMaximizeCallback() {
            @Override
            public void invoke(long window, boolean maximized) {
                for (GLFWWindowMaximizeCallbackI cb : windowMaximizeCallbacks)
                    cb.invoke(window, maximized);
            }
        };
        glfwSetWindowMaximizeCallback(window, wmcb);

        GLFWWindowPosCallback wpcb = new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int xpos, int ypos) {
                for (GLFWWindowPosCallbackI cb : windowPosCallbacks)
                    cb.invoke(window, xpos, ypos);
            }
        };
        glfwSetWindowPosCallback(window, wpcb);

        GLFWWindowRefreshCallback wrcb = new GLFWWindowRefreshCallback() {
            @Override
            public void invoke(long window) {
                for (GLFWWindowRefreshCallbackI cb : windowRefreshCallbacks)
                    cb.invoke(window);
            }
        };
        glfwSetWindowRefreshCallback(window, wrcb);

        GLFWWindowSizeCallback wscb = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                for (GLFWWindowSizeCallbackI cb : windowSizeCallbacks)
                    cb.invoke(window, width, height);
            }
        };
        glfwSetWindowSizeCallback(window, wscb);

        GLFWWindowContentScaleCallback wcscb = new GLFWWindowContentScaleCallback() {
            @Override
            public void invoke(long window, float xscale, float yscale) {
                for (GLFWWindowContentScaleCallbackI cb : windowContentScaleCallbacks)
                    cb.invoke(window, xscale, yscale);
            }
        };
        glfwSetWindowContentScaleCallback(window, wcscb);

        autoCloseableCbs.addAll(List.of(kcb, ccb, dcb, pcb, scb, cmcb, cecb, cpcb, fscb, mbcb, wccb, wfcb, wicb, wmcb, wpcb, wrcb, wscb, wcscb));

    }

    /** @return true if the specified key is pressed */
    public boolean isKeyPressed(int key) {
        switch (glfwGetKey(window, key)) {
            case GLFW_REPEAT, GLFW_PRESS -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    /** Overload for {@link GLFWWindow#getVkSurface(VkInstance)} accepting {@link VkInstanceContext} instead of {@link VkInstance} */
    public long getVkSurface(VkInstanceContext instance) {
        return getVkSurface(instance.instance());
    }

    /** Gets the vulkan surface associated with this window if it does not exist it makes a new one
     * @return the vulkan surface of this window */
    public long getVkSurface(VkInstance instance) {
        if (surfaces.containsKey(instance))
            return surfaces.get(instance);

        try (MemoryStack stack = stackPush()) {
            LongBuffer lb = stack.callocLong(1);
            GLFWVulkan.glfwCreateWindowSurface(instance, window, null, lb);
            surfaces.put(instance, lb.get(0));
            return lb.get(0);
        }
    }

    /** Closes the window. */
    public void close() {
        glfwSetWindowShouldClose(window, true);

        for (AutoCloseable closeable : autoCloseableCbs) {
	        try {
		        closeable.close();
	        } catch (Exception e) {
                System.err.println(e.getMessage());
	        }
        }
    }

    /** @return true if the window should close */
    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    /** Gets the current state of a mouse button defined in {@link GlfwEnums.MouseButtonState}
     * @param mb the mouse button of which state to query
     * @return the state of the mouse button  */
    public GlfwEnums.MouseButtonState getMouseButtonState(GlfwEnums.MouseButton mb) {
        return GlfwEnums.MouseButtonState.fromConstant(glfwGetMouseButton(window, mb.constant));
    }

    /** Gets the current state of the mouse cursor defined in {@link GlfwEnums.CursorState}
     * @return the current state of the mouse cursor */
    public GlfwEnums.CursorState getCursorState() {
        return GlfwEnums.CursorState.fromConstant(glfwGetInputMode(window, GLFW_CURSOR));
    }

    /** Sets the cursor mode to one of the modes defined in {@link GlfwEnums.CursorState}
     * @param state the state that the cursor will be set to */
    public void setCursorState(GlfwEnums.CursorState state) {
        glfwSetInputMode(window, GLFW_CURSOR, state.constant);
    }

    /** Sets the position of the cursor
     * @param pos the new position of the cursor
     * @implNote FUCK WAYLAND */
    public void setCursorPosition(Vector2d pos) {
        glfwSetCursorPos(window, pos.x, pos.y);
    }

    /** Gets the current position of the cursor
     * @return the current position of the cursor */
    public Vector2d getCursorPosition() {
        double[] x = new double[1];
        double[] y = new double[1];

        glfwGetCursorPos(window, x, y);

        return new Vector2d(x[0], y[0]);
    }

    /** Adds a key callback, triggered when a keyboard key is pressed.
     *
     * @since 0.0.1
     */
    public void addKeyCallback(GLFWKeyCallbackI callback) {
        keyCallbacks.add(callback);
    }
    /** Adds a character callback, triggered when a character is input.
     *
     * @since 0.0.1
     */
    public void addCharCallback(GLFWCharCallbackI callback) {
        charCallbacks.add(callback);
    }

    /** Adds a drop callback, triggered when files are dropped onto the window.
     *
     * @since 0.0.1
     */
    public void addDropCallback(GLFWDropCallbackI callback) {
        dropCallbacks.add(callback);
    }

    /** Adds a character mods callback, triggered when a character with modifiers is input.
     *
     * @since 0.0.1
     */
    public void addCharModsCallback(GLFWCharModsCallbackI callback) {
        charModsCallbacks.add(callback);
    }

    /** Adds a preedit callback, triggered during text composition.
     *
     * @since 0.0.1
     */
    public void addPreeditCallback(GLFWPreeditCallbackI callback) {
        preeditCallbacks.add(callback);
    }

    /** Adds a scroll callback, triggered when the scroll wheel is used.
     *
     * @since 0.0.1
     */
    public void addScrollCallback(GLFWScrollCallbackI callback) {
        scrollCallbacks.add(callback);
    }

    /** Adds a cursor enter callback, triggered when the cursor enters or leaves the window.
     *
     * @since 0.0.1
     */
    public void addCursorEnterCallback(GLFWCursorEnterCallbackI callback) {
        cursorEnterCallbacks.add(callback);
    }

    /** Adds a cursor position callback, triggered when the cursor is moved.
     *
     * @since 0.0.1
     */
    public void addCursorPosCallback(GLFWCursorPosCallbackI callback) {
        cursorPosCallbacks.add(callback);
    }

    /** Adds a framebuffer size callback, triggered when the framebuffer size changes.
     *
     * @since 0.0.1
     */
    public void addFramebufferSizeCallback(GLFWFramebufferSizeCallbackI callback) {
        framebufferSizeCallbacks.add(callback);
    }

    /** Adds a mouse button callback, triggered when a mouse button is pressed or released.
     *
     * @since 0.0.1
     */
    public void addMouseButtonCallback(GLFWMouseButtonCallbackI callback) {
        mouseButtonCallbacks.add(callback);
    }

    /** Adds a window close callback, triggered when the window is requested to close.
     *
     * @since 0.0.1
     */
    public void addWindowCloseCallback(GLFWWindowCloseCallbackI callback) {
        windowCloseCallbacks.add(callback);
    }

    /** Adds a window focus callback, triggered when the window gains or loses focus.
     *
     * @since 0.0.1
     */
    public void addWindowFocusCallback(GLFWWindowFocusCallbackI callback) {
        windowFocusCallbacks.add(callback);
    }

    /** Adds a window iconify callback, triggered when the window is minimized or restored.
     *
     * @since 0.0.1
     */
    public void addWindowIconifyCallback(GLFWWindowIconifyCallbackI callback) {
        windowIconifyCallbacks.add(callback);
    }

    /** Adds a window maximize callback, triggered when the window is maximized or restored.
     *
     * @since 0.0.1
     */
    public void addWindowMaximizeCallback(GLFWWindowMaximizeCallbackI callback) {
        windowMaximizeCallbacks.add(callback);
    }

    /** Adds a window position callback, triggered when the window position changes.
     *
     * @since 0.0.1
     */
    public void addWindowPosCallback(GLFWWindowPosCallbackI callback) {
        windowPosCallbacks.add(callback);
    }

    /** Adds a window refresh callback, triggered when the window content needs to be redrawn.
     *
     * @since 0.0.1
     */
    public void addWindowRefreshCallback(GLFWWindowRefreshCallbackI callback) {
        windowRefreshCallbacks.add(callback);
    }

    /** Adds a window size callback, triggered when the window size changes.
     *
     * @since 0.0.1
     */
    public void addWindowSizeCallback(GLFWWindowSizeCallbackI callback) {
        windowSizeCallbacks.add(callback);
    }

    /** Adds a window content scale callback, triggered when the content scale of the window changes.
     *
     * @since 0.0.1
     */
    public void addWindowContentScaleCallback(GLFWWindowContentScaleCallbackI callback) {
        windowContentScaleCallbacks.add(callback);
    }

    /** Shows the window.
     * @since 0.0.1
     */
    public void show() {
        glfwShowWindow(window);
    }
    /** Hides the window.
     * @since 0.0.1
     */
    public void hide() {
        glfwHideWindow(window);
    }
    /** @return True if the window is visible else returns false.
     * @since 0.0.1
     */
    public boolean getVisible() {
        return fromGLFWBool(glfwGetWindowAttrib(window, GLFW_VISIBLE));
    }
    /** @param visible Sets the visibility of the window.
     * @since 0.0.1
     */
    public void setVisible(boolean visible) {
        if (visible) {
            glfwShowWindow(window);
        } else {
            glfwHideWindow(window);
        }
    }

    /**
     * @param title Sets the title of the window.
     * @since 0.0.1
     * */
    public void setTitle(String title) {
        glfwSetWindowTitle(window, title);
    }

    /** @param resizable Sets the resizability of the window.
     * @since 0.0.1
     */
    public void setResizable(boolean resizable) {
        glfwSetWindowAttrib(window, GLFW_RESIZABLE, ToGLFWBool(resizable));
    }
    /** @return True if the window is resizable else returns false.
     * @since 0.0.1
     */
    public boolean isResizable() {
        return fromGLFWBool(glfwGetWindowAttrib(window, GLFW_RESIZABLE));
    }

    /** @param position Sets the position of the window.
     * @since 0.0.1
     */
    public void setPosition(Vector2i position) {
        glfwSetWindowPos(window, position.x, position.y);
    }
    /** @return The position of the window in a {@link Vector2f}.
     * @since 0.0.1
     */
    public Vector2i getPosition() {
        Vector2i position = new Vector2i();
        int[] x = new int[1], y = new int[1];
        glfwGetWindowPos(window, x,y);
        position.x = x[0];
        position.y = y[0];
        return position;
    }
    /** @param size Sets the size of the window, where {@link Vector2f#x} is the width, {@link Vector2f#y} is the height.
     * @since 0.0.1
     */
    public void setSize(Vector2i size) {
        glfwSetWindowSize(window, size.x, size.y);
    }

    /** @return The size of the window in a {@link Vector2f}, where {@link Vector2f#x} is the width, {@link Vector2f#y} is the height.
     * @since 0.0.1
     */
    public Vector2i getSize() {
        Vector2i size = new Vector2i();
        int[] width = new int[1], height = new int[1];
        glfwGetWindowSize(window, width, height);
        size.x = width[0];
        size.y = height[0];
        return size;
    }

    /** @return the size of the window's framebuffer */
    public Vector2i getFrameBufferSize() {
        int[] x = new int[1];
        int[] y = new int[1];

        glfwGetFramebufferSize(window, x, y);
        return new Vector2i(x[0], y[0]);
    }

    /** @return the total amount of mouse scroll in the window */
    public float getTotalScroll() {
        return totalScroll;
    }

    /** @return the handle to the native window use with caution */
    public long getWindow() {
        return window;
    }

    private boolean fullscreen = false;
    private int windowedX, windowedY, windowedW, windowedH;

    public void toggleFullScreen() {
        if (!fullscreen) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer x = stack.mallocInt(1);
                IntBuffer y = stack.mallocInt(1);
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);

                glfwGetWindowPos(window, x, y);
                glfwGetWindowSize(window, w, h);

                windowedX = x.get(0);
                windowedY = y.get(0);
                windowedW = w.get(0);
                windowedH = h.get(0);
            }

            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode mode = glfwGetVideoMode(monitor);

            glfwSetWindowMonitor(window, monitor,
                    0, 0,
                    mode.width(), mode.height(),
                    mode.refreshRate());
        } else {
            glfwSetWindowMonitor(window, 0,
                    windowedX, windowedY,
                    windowedW, windowedH,
                    0);
        }

        fullscreen = !fullscreen;
    }

}