#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTexCoords;
out vec2 TexCoords;
uniform mat4 u_projection;

void main() {
    TexCoords = aTexCoords;
    gl_Position = u_projection * vec4(aPos, 0.0, 1.0);
}