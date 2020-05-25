#version 120

// multiplies all colors by the color vector
uniform sampler2D texture;
uniform vec2 resolution;
uniform vec3 multiplier;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec4 pixelColor = texture2D(texture, uv);
    pixelColor.r *= multiplier.r;
    pixelColor.g *= multiplier.g;
    pixelColor.b *= multiplier.b;
    gl_FragColor = pixelColor;
}
