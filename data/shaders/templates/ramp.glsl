#version 120

uniform sampler2D ramp;
uniform sampler2D texture;
uniform vec2 resolution;

vec4 rampColor(float pct){
    return texture2D(ramp, vec2(0.5, clamp(pct, 0., 1.)));
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    gl_FragColor = rampColor(uv.y);
}
