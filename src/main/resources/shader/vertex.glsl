// see https://www.khronos.org/opengl/wiki/Vertex_Shader

#version 400 core

uniform float inTime;
uniform mat4 inMatrix;
out float fromVertexShaderToFragmentShader;
in vec3 inPos;

in vec3 inColor;
out vec3 color;

void main()
{
    gl_Position = vec4(inPos, 1.0) * inMatrix;
    fromVertexShaderToFragmentShader = inPos.x + 0.5;
    color = inColor;
}
