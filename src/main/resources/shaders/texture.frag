#version 330 core

in vec2 vTexCoord;
out vec4 FragColor;

uniform sampler2D uTexture;
uniform vec4 uColor;
uniform bool uUseTexture;
uniform vec2 uUVMin;
uniform vec2 uUVMax;
uniform bool uSampleAlphaOnly;

void main() {
    vec2 uv = mix(uUVMin, uUVMax, vTexCoord);
    vec4 sampled = uUseTexture ? texture(uTexture, uv) : vec4(1.0);
    if (uSampleAlphaOnly) {
        float alpha = sampled.r;
        sampled = vec4(1.0, 1.0, 1.0, alpha);
    }
    FragColor = uColor * sampled;
}
