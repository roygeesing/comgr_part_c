// see https://www.khronos.org/opengl/wiki/Vertex_Shader

#version 400 core

uniform float inTime;
uniform mat4 mvpMatrix;
uniform mat3 normalMatrix;

in vec3 inPos;

in vec2 inSt;
out vec2 st;

in vec3 inNormal;
out vec3 normal;

void main()
{
    gl_Position = vec4(inPos, 1.0) * mvpMatrix;
    st = inSt;
    normal = inNormal * normalMatrix * vec3(-1, 1, 1);
}
