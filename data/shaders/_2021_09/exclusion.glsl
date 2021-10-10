#version 120

uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;
const float pi = 3.1415;

vec3 a = vec3(0.5);
vec3 b = vec3(0.5);
vec3 c = vec3(1);
vec3 d = vec3(0, 0.1, 0.2);
// cosine based palette, 4 vec3 params
vec3 palette(in float t, in vec3 a, in vec3 b, in vec3 c, in vec3 d)
{
    return a + b*cos(6.28318*(c*t+d));
}

float random(float p){
    return fract(sin(12235.12146*p));
}

void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    float dist = length(uv);
    float t =  time;
    float aCount = 20.;
    float dCount = 15.;
    float aCoord = fract(floor((0.5+0.5*(atan(uv.y, uv.x) / pi)) *  aCount) / aCount);
    float bCoord = fract(floor(dist * 1.55 * dCount) / dCount);
    float random = random(aCoord+bCoord);
    vec2 id = vec2(
        0.5*aCoord + random*(0.1+0.1*cos(t)) + 0.1*cos(t),
        0.5*bCoord + random*(0.1+0.1*sin(t)) + 0.1*sin(t*0.1))
    ;
    vec3 col = palette(id.y+id.x, a, b, c, d);
    col -= 0.2*fract(id.x*1.);
    gl_FragColor = vec4(col, 1.);
}