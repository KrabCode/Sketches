uniform sampler2D texture;
uniform sampler2D imgDoggo;
uniform vec2 res;
uniform float t;
uniform vec2 iMouse;

#define tau 6.28318

// Hash without Sine by David Hoskins (MIT License)
float hash12(vec2 p)
{
    vec3 p3  = fract(vec3(p.xyx) * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}
float hash13(vec3 p3)
{
    p3  = fract(p3 * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

vec3 backbufferFaded(vec2 uv, float mult, float sub){
    return texture2D(texture, uv).rgb * mult - sub;
}

// noise is from here:
// https://www.shadertoy.com/view/4dS3Wd by Morgan McGuire @morgan3d!

// Precision-adjusted variations of https://www.shadertoy.com/view/4djSRW
float hash(float p) { p = fract(p * 0.011); p *= p + 7.5; p *= p + p; return fract(p); }
float hash(vec2 p) {vec3 p3 = fract(vec3(p.xyx) * 0.13); p3 += dot(p3, p3.yzx + 3.333); return fract((p3.x + p3.y) * p3.z); }

float noise(float x) {
    float i = floor(x);
    float f = fract(x);
    float u = f * f * (3.0 - 2.0 * f);
    return mix(hash(i), hash(i + 1.0), u);
}

float noiseSample(vec2 x) {
    vec2 i = floor(x);
    vec2 f = fract(x);

    // Four corners in 2D of a tile
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));

    // Simple 2D lerp using smoothstep envelope between the values.
    // return vec3(mix(mix(a, b, smoothstep(0.0, 1.0, f.x)),
    //			mix(c, d, smoothstep(0.0, 1.0, f.x)),
    //			smoothstep(0.0, 1.0, f.y)));

    // Same code, with the clamps in smoothstep and common subexpressions
    // optimized away.
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(vec2 uv){
    uv.y += t*.1;
    float sum = 0.;
    float freq = 1.;
    float amp = 1.;
    for(int i = 0; i < 5; i++){
        sum += noiseSample(uv*freq)*amp;
        freq *= 2.5;
        amp *= .5;
    }
    return sum;
}

vec2 wind(vec2 uv, float mag, float angleRange){
    float windNoise = fbm(uv);
    float windAngle = angleRange*windNoise;
    vec2 windOffset = vec2(cos(windAngle), sin(windAngle))*mag;
    return windOffset;
}

void main(){
    vec2 uv = gl_FragCoord.xy / res.xy;
    vec2 cv = (gl_FragCoord.xy - .5 * res.xy) / res.y;

    vec2 imgUV = vec2(uv.x-.1, 1.-uv.y);
    imgUV.x *= 1.35;
    imgUV *= 1.2;
    imgUV += vec2(-.15, -.1);
    float texDoggo = texture2D(imgDoggo, imgUV).r;

    if((texDoggo > .2 && texDoggo < .8) || (imgUV.x < 0.05 || imgUV.x > .95)){
        texDoggo = 0.2;
    }

    float hash = hash13(vec3(cv.xy*1000., 0));
    hash *= step(hash, .1);
//    hash *= texDoggo;
    hash *= min(smoothstep(0.5,.4, length(cv)), texDoggo);
    hash = pow(hash, 0.7);
    vec3 col = hash*4. + backbufferFaded(
        uv+wind(uv, .0005, tau*1.0),
        1.005, .05); // mult, sub
    gl_FragColor = vec4(col, 1.);
}

