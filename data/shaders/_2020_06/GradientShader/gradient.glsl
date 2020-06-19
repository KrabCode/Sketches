#version 120
#define PI 3.14159

uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

uniform sampler2D gradient;

vec4 rampColor(float pct){
    return texture2D(gradient, vec2(0.5, clamp(pct, 0., 1.)));
}

void main() {
    float t = time*0.25;
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    cv *= 5;
    float distanceFromCenter = length(cv);
    float a = atan(cv.y, cv.x)/PI;
    float p = mod(distanceFromCenter-t+a, 1.);
    gl_FragColor = rampColor(uv.y);
}
