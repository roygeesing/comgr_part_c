// see https://www.khronos.org/opengl/wiki/Fragment_Shader

#version 400 core

uniform vec3 color;
uniform vec3 lightDirection;
uniform vec3 cameraDirection;

out vec4 outColor;

in vec2 st;
in vec3 normal;

vec3 diffuse() {
    return color; // * max(dot(normal, lightDirection), 0);
}

vec3 specular() {
    vec3 r = reflect(lightDirection, normal);
    return vec3(1, 1, 1) * pow(max(dot(r, cameraDirection), 0), 80);
}

void main()
{
    outColor = vec4(diffuse(), 1); // + vec4(specular(), 1);
    //    outColor = vec4(diffuse(), 1);
    //    outColor = vec4(specular(), 1.0);
}
