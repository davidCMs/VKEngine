#version 450

layout(location = 0) out vec4 outColor;
layout(location = 0) in vec3 fragColor;
layout(location = 1) in float frame;

void main() {

    outColor = vec4(fragColor*sin(frame*0.0001)*.5+0.5, 1.0);
}