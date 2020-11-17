uniform sampler2D texture;
uniform sampler2D img;
uniform sampler2D flow;
uniform vec2 resolution;
uniform vec2 imgRes;
uniform float time;

#define PI 3.14159
#define TAU PI*2.

float cubicPulse(float c, float w, float x )
{
    x = abs(x - c);
    if( x>w ) return 0.0;
    x /= w;
    return 1.0 - x*x*(3.0-2.0*x);
}

float debugCrossFade(vec2 uv, float timeA, float timeB, float timeCrossFaded){
    float pulseA = cubicPulse(timeA, 0.1, uv.y);
    float pulseB = cubicPulse(timeB, 0.1, uv.y);
    float fadedValue = mix(pulseA, pulseB, timeCrossFaded);
    return fadedValue;
}

void main(){
    vec2 uv = gl_FragCoord.xy / imgRes.xy;
    uv += vec2(0.055, 0);
    uv *= 3.8;
    uv.y = 1.-uv.y;
    float timeSpeed = 1./TAU;
    float t = time * timeSpeed;
    float timeA = fract(t);
    float timeB = fract(t + 0.5);
    float timeCrossFaded = .5+.5*sin(PI * .5 + TAU * t);
    vec2 flowVector = 0.12 * (-.5+texture(flow, uv).gr);
    vec2 offsetA = flowVector*timeA;
    vec2 offsetB = flowVector*timeB;
    vec3 sampleA = texture(img, uv+offsetA).rgb;
    vec3 sampleB = texture(img, uv+offsetB).rgb;
    vec3 sampleCrossFaded = mix(sampleA, sampleB, timeCrossFaded);
    gl_FragColor = vec4(sampleCrossFaded, 1.);
//    gl_FragColor = vec4(vec3(debugCrossFade(uv, timeA, timeB, timeCrossFaded)), 1.);
}