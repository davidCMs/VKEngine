package dev.davidCMs.vkengine.window;

/**
 * The {@code WindowException} exception is used for {@link GLFWWindow} related problems
 *
 * @author davidCMs
 * @since 0.0.1
 * */
public class GLFWException extends RuntimeException {
    public GLFWException(String message) {
        super(message);
    }
}
