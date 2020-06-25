uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

float hash(vec2 seed) {
    return fract(sin(seed.x * 1.121f + seed.y*1.02) * 454.123f);
}

float valueNoise(vec2 cv){
    vec2 gv = fract(cv);

    float topRight = hash(floor(cv) + vec2(1, 1));
    float topLeft = hash(floor(cv) + vec2(0, 1));
    float botRight = hash(floor(cv) + vec2(1, 0));
    float botLeft = hash(floor(cv));

    float blend = mix(
    mix(topLeft, topRight, fract(gv.x)),
    mix(botLeft, botRight, fract(gv.x)),
    fract(-gv.y)
    );
    blend = smoothstep(0.0, 0.8, blend);
    blend = pow(blend, 2.5);
    return blend;
}

float noise(vec2 cv, float amp, float freq){
    return amp * valueNoise(cv*freq);
}

float fbm(vec2 cv){
    return noise(cv, 0.5, 1.)
         + noise(cv + 10, 0.5, 2.)
         + noise(cv + 20, 0.25, 8.0);
}

void main(){
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    float n = fbm(cv+fbm(cv*1+vec2(0, -time*.05)));
    gl_FragColor = vec4(vec3(n), 1.);
}