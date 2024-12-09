// see https://www.khronos.org/opengl/wiki/Vertex_Shader

#version 400 core

uniform float inTime;
uniform mat4 inMatrix;

in vec3 inPos;

in vec2 inSt;
out vec2 st;

in vec3 inNormal;
out vec3 normal;

void main()
{
    gl_Position = vec4(inPos, 1.0) * inMatrix;
    st = inSt;
//    normal = inNormal * mat3((transpose(inverse(inMatrix)) * determinant(inMatrix)));
    normal = inNormal * mat3(inMatrix);
}
