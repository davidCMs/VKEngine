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

    gl_Position = vec4(pos, 1.0);
    verUvOut = vertUv;
}