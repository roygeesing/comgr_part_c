// see https://www.khronos.org/opengl/wiki/Fragment_Shader

#version 400 core

out vec4 outColor;
in float fromVertexShaderToFragmentShader;
in vec3 color;

void main()
{
    outColor = vec4(color, 1.0);
}
