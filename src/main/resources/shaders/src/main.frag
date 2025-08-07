#version 450

layout(push_constant) uniform PushConstants {
    float frame;
    float time;
    ivec2 resolution;
} pc;

layout(location = 0) in vec3 color;
layout(location = 0) out vec4 outColor;

void main() {

    vec2 coord = vec2(gl_FragCoord.x, pc.resolution.y - gl_FragCoord.y);

    vec4 c = vec4(coord.xy, 0, 0.9);

    c.xyz *= sin(color * pc.time);

    outColor = c;

}