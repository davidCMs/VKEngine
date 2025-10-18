#version 450

layout(push_constant) uniform PushConstants {
    float frame;
    float time;
    ivec2 resolution;
    vec2 mousePos;
    int mbMask;
    float totalScroll;
} pc;

layout(location = 0) in vec3 color;
layout(location = 0) out vec4 outColor;

float remap(float inMin, float inMax, float outMin, float outMax, float value) {
    return outMin + (value - inMin) * (outMax - outMin) / (inMax - inMin);
}

vec2 remap(vec2 inMin, vec2 inMax, vec2 outMin, vec2 outMax, vec2 value) {
    return outMin + (value - inMin) * (outMax - outMin) / (inMax - inMin);
}

vec3 remap(vec3 inMin, vec3 inMax, vec3 outMin, vec3 outMax, vec3 value) {
    return outMin + (value - inMin) * (outMax - outMin) / (inMax - inMin);
}

vec4 remap(vec4 inMin, vec4 inMax, vec4 outMin, vec4 outMax, vec4 value) {
    return outMin + (value - inMin) * (outMax - outMin) / (inMax - inMin);
}

vec4 mSet(vec2 uv, int itter) {
    vec2 z = vec2(0);
    vec2 c = uv;

    int i;
    for (i = 0; i < itter ; i++) {
        vec2 z2 = vec2(
        z.x * z.x,
        z.y * z.y
        );

        if (z2.x + z2.y > 4.0f) {
            float s = remap(0, itter, 0, 1, i);
            return vec4(0, 0.5, 1, 1)*s;
        }

        vec2 zn = vec2(
        z2.x - z2.y + c.x,
        2 * z.x * z.y + c.y
        );

        z = zn;
    }

    return vec4(1);
}

void main() {

    vec2 coord = vec2(gl_FragCoord.x, pc.resolution.y - gl_FragCoord.y);

    vec2 uv = coord / vec2(pc.resolution);

    float zoom = pc.totalScroll;
    if (zoom == 0 || zoom == 1.5)
            zoom = 1;
    if (zoom < 1)
            zoom = 1;

    vec2 mouseCoord = vec2(pc.mousePos.x, pc.resolution.y - pc.mousePos.y);
    mouseCoord /= vec2(pc.resolution);
    uv = (uv - mouseCoord) / (zoom*zoom) + mouseCoord;

    uv = vec2(
        uv.x * 3.5 - 2.5,
        uv.y * 2.0 - 1.0
    );

    int timeSpeed = 2;
    float time;

    if (pc.time < 1)
        time = 1;
    else
        time = pc.time * timeSpeed;

    vec4 prvItter = mSet(uv, int(time)-timeSpeed);
    vec4 curItter = mSet(uv, int(time));

    vec3 c = mix(prvItter.xyz, curItter.xyz, vec3(fract(time)));

    if (pc.mbMask == 1)
        c.r += 0.5;

    outColor = vec4(c, 1);

}