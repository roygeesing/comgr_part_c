// see https://www.khronos.org/opengl/wiki/Fragment_Shader

#version 400 core

uniform sampler2D imageTexture;
uniform vec3 lightDirection;
uniform vec3 cameraDirection;

out vec4 outColor;

in vec2 st;
in vec3 normal;

vec4 diffuse() {
    vec4 texColor = texture(imageTexture, st);
    return texColor;
}

void main()
{
    outColor = diffuse();
}
