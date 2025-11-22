#version 330 core

in vec2 vTexCoord;
out vec4 FragColor;

uniform sampler2D uTexture;
uniform vec4 uColor;
uniform bool uUseTexture;
uniform vec2 uUVMin;
uniform vec2 uUVMax;

void main() {
    vec2 uv = mix(uUVMin, uUVMax, vTexCoord);
    vec4 sampled = uUseTexture ? texture(uTexture, uv) : vec4(1.0);
    FragColor = uColor * sampled;
}
