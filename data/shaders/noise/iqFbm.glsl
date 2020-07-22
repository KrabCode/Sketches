uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;


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
    for (int i = 0; i < 6; i++){
        sum += amp*(1-2*iqNoise(p*freq));
        freq *= 2.0;
        amp *= .5;
    }
    return sum;
}

float fbm(vec2 p){
    float sum = 0.;
    float freq = 1.;
    float amp = 0.5;
    for (int i = 0; i < 6; i++){
        sum += amp*(1-2*iqNoise(vec3(p*freq, 0.)));
        freq *= 2.0;
        amp *= .5;
    }
    return sum;
}

float pattern( in vec2 p, out vec2 q, out vec2 r )
{
    q.x = fbm( p + vec2(0.0,0.0) );
    q.y = fbm( p + vec2(5.2,1.3) );

    r.x = fbm( p + 4.0*q + vec2(1.7+time*.2,9.2) );
    r.y = fbm( p + 4.0*q + vec2(8.3,2.8+time*.1) );

    return fbm( p + 4.0*r );
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    vec2 q, r;
    vec3 col = vec3(pattern(uv, q, r));
    gl_FragColor = vec4(col, 1.);
}