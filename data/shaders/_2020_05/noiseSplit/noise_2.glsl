uniform vec2 resolution;
uniform float time;
uniform float colorStrength;
uniform sampler2D ramp;
uniform sampler2D texture;

vec4 rampColor(float pct){
    return texture2D(ramp, vec2(0.5, clamp(pct, 0., 1.)));
}

vec2 g( vec2 n ) { return sin(n.x*n.y*vec2(12,17)+vec2(1,2)); }
//vec2 g( vec2 n ) { return sin(n.x*n.y+vec2(0,1.571)); } // if you want the gradients to lay on a circle

float noise(vec2 p){
    const float kF = 6;  // make 6 to see worms

    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f*f*(3.0-2.0*f);
    return mix(mix(sin(kF*dot(p,g(i+vec2(0,0)))),
    sin(kF*dot(p,g(i+vec2(1,0)))),f.x),
    mix(sin(kF*dot(p,g(i+vec2(0,1)))),
    sin(kF*dot(p,g(i+vec2(1,1)))),f.x),f.y);
}

float noise(vec2 p, float amp, float freq){
    return amp*noise(p*freq);
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    vec4 origColor = texture2D(texture, uv);
    float d = length(cv);
    float t = time/4;
    float tr = .5;
    vec2 timeWheel = vec2(cos(t), sin(t))*tr;
    vec2 tempUV = uv + vec2(0.0, -0.3)*t;
    float pct = abs(noise(tempUV, 1., 0.5) +
                    noise(tempUV, 0.5, 1.) +
                    noise(tempUV, 0.25, 10.) +
                    noise(tempUV+timeWheel, 0.1, 10.));
    float outerStart = 0.0;
    float outerEnd = 0.8;
    pct *= smoothstep(outerEnd, outerStart, d);
    vec4 newColor = rampColor(pct);
    gl_FragColor = origColor+colorStrength*newColor;
}