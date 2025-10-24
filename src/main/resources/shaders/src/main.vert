#version 450

layout(push_constant) uniform PushConstants {
    float frame;
    float time;
    ivec2 resolution;
    vec2 mousePos;
    int mbMask;
    float totalScroll;
} pc;

layout(location = 0) in vec3 vertPos;
layout(location = 1) in vec2 vertUv;
layout(location = 0) out vec3 fragColor;
layout(location = 1) out vec2 verUvOut;

void main() {
    vec3 pos = vertPos;

    // The 'pos' array holds the geometry's local coordinates.
    // We must apply a Model-View transform to place it into VIEW SPACE.
    // Assuming no rotation/scaling (Model-View matrix is Identity):

    // CRITICAL: Move the object back along the NEGATIVE Z axis to be visible.
    // Vulkan/OpenGL cameras look down -Z. This value must be between near and far, e.g., -5.0.
    pos.z -= 1; // OR pos.z = vertPos.z - 5.0; OR just pos.z = -5.0; for testing

    float f = 1.0 / tan(radians(60.0) * 0.5);

    // CRITICAL: Use the actual resolution for the aspect ratio.
    float aspect = float(pc.resolution.x) / float(pc.resolution.y);

    float near = 0.01;
    float far = 1000.0;
    float range = far - near; // Pre-calculate for clarity

    // FINAL CANONICAL VULKAN PERSPECTIVE PROJECTION MATRIX:
    mat4 proj = mat4(
    // X-axis scale
    f / aspect, 0.0, 0.0, 0.0,

    // Y-axis scale (CRITICAL: MUST be negative for Vulkan's inverted Y-NDC)
    0.0, -f, 0.0, 0.0,

    // Z-axis mapping (CRITICAL: Maps Z to [0, 1] depth range)
    0.0, 0.0, far / range, -(far * near) / range,

    // W component (for perspective division)
    0.0, 0.0, -1.0, 0.0
    );

    gl_Position = proj * vec4(pos, 1.0);
    verUvOut = vertUv;
}