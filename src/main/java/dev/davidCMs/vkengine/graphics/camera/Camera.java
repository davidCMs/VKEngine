package dev.davidCMs.vkengine.graphics.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Matrix4f view = new Matrix4f();
    private float fov = 90, farP = 1000, nearP = 0.01f;

    public Camera lookAt(Vector3f position, Vector3f up, Vector3f point) {
        view.lookAt(position, up, point);
        return this;
    }

}
