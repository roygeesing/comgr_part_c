// see https://www.khronos.org/opengl/wiki/Fragment_Shader

#version 400 core

uniform sampler2D imageTexture;

out vec4 outColor;

in vec2 st;

void main()
{
//    outColor = texture(imageTexture, st);
    outColor = vec4(vec3(1.0, 1.0, 1.0) - texture(imageTexture, st).rgb, 1.0);
}
