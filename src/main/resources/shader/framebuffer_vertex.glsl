// see https://www.khronos.org/opengl/wiki/Vertex_Shader

#version 400 core

in vec3 inPos;

in vec2 inSt;
out vec2 st;

void main()
{
    gl_Position = vec4(inPos, 1.0);
    st = inSt;
}
