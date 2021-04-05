uniform sampler2D texture;
uniform sampler2D gradient;
uniform vec2 resolution;
uniform float time;

const float pi = 3.14159;


// IQ noise
float hash(float n)
{
    return fract(sin(n)*43758.5453);
}

float iqNoise(vec3 x){
    // The noise function returns a value in the range -1.0f -> 1.0f
    vec3 p = floor(x);
    vec3 f = fract(x);
    f  = f*f*(3.0-2.0*f);
    float n = p.x + p.y*57.0 + 113.0*p.z;
    return mix(mix(mix(hash(n+0.0), hash(n+1.0), f.x),
    mix(hash(n+57.0), hash(n+58.0), f.x), f.y),
    mix(mix(hash(n+113.0), hash(n+114.0), f.x),
    mix(hash(n+170.0), hash(n+171.0), f.x), f.y), f.z);
}


float fbm(vec3 p){
    float sum = 0.;
    float freq = 1.;
    float amp = 0.5;
    for (int i = 0; i < 4; i++){
        sum += amp*(1-2*iqNoise(p*freq));
        freq *= 2.0;
        amp *= .5;
    }
    return sum;
}

vec3 gradientColor(float x){
    x = abs(x);
    x = fract(x);
    return texture2D(gradient, vec2(0.5, fract(x))).rgb;
}

mat2 rotate2d(float angle){
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}


void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    float a = cos(atan(cv.y, cv.x));
    vec2 rot = cv * rotate2d(a);
    float pct = 0.5+0.5*fbm(vec3(rot.x*15.0, time*.2, 0.5*rot.y+time*0.2));

    vec3 clr = gradientColor(pct);

    clr.rgb *= smoothstep(0.75, 0.35, length(cv));
    gl_FragColor = vec4(clr, 1.);
}