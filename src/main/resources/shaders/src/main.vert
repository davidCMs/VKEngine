#version 450

layout(push_constant) uniform PushConstants {
    float frame;
} pc;

layout(location = 0) out vec3 fragColor;
layout(location = 1) out float frame;

vec2 positions[3] = vec2[](
    vec2(0.0, -0.5),
    vec2(0.5, 0.5),
    vec2(-0.5, 0.5)
);

vec3 colors[3] = vec3[](
vec3(1.0, 0.0, 0.0),
vec3(0.0, 1.0, 0.0),
vec3(0.0, 0.0, 1.0)
);

void main() {
    float rad = radians(mod(pc.frame*0.01, 360.0));

    float c = cos(rad);
    float s = sin(rad);

    mat2 rot = mat2(
        c, -s,
        s,  c
    );

    vec2 rotatedPos = rot * positions[gl_VertexIndex];

    gl_Position = vec4(rotatedPos, 0.0, 1.0);
    fragColor = colors[gl_VertexIndex];
    frame = pc.frame;
}
