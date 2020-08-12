uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

// "ShaderToy Tutorial - Ray Marching for Dummies!"
// by Martijn Steinrucken aka BigWings/CountFrolic - 2018
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
//
// This shader is part of a tutorial on YouTube
// https://youtu.be/PGtv-dBi2wE

#define MAX_STEPS 1000
#define MAX_DIST 9001.
#define SURF_DIST .001
#define NORM_DIST .01

uniform sampler2D gradient;

vec4 gradientColor(float pct){
    return texture2D(gradient, vec2(0.5, clamp(pct, 0., 1.)));
}

vec3 repeat(vec3 p, float c){
    return p;
//    return mod(p+0.5*c,c)-0.5*c;
}

float GetDist(vec3 p) {
    vec4 s = vec4(1, 1, 6, 1.0);

    float sphereDist =  length(repeat(p, 1.)-s.xyz)-s.w;
    float planeDist = p.y;

    float d = min(sphereDist, planeDist);
//    float d = sphereDist;
    return d;
}

float RayMarch(vec3 ro, vec3 rd) {
    float dO=0.;

    for(int i=0; i<MAX_STEPS; i++) {
        vec3 p = ro + rd*dO;
        float dS = GetDist(p);
        dO += dS;
        if(dO>MAX_DIST || dS<SURF_DIST) break;
    }

    return dO;
}

vec3 GetNormal(vec3 p) {
    float d = GetDist(p);
    vec2 e = vec2(NORM_DIST, 0);

    vec3 n = d - vec3(
    GetDist(p-e.xyy),
    GetDist(p-e.yxy),
    GetDist(p-e.yyx));

    return normalize(n);
}

float GetLight(vec3 p) {
    vec3 lightPos = vec3(0, 5, 6);
    lightPos.xz += vec2(sin(time), cos(time))*2.;
    vec3 l = normalize(lightPos-p);
    vec3 n = GetNormal(p);
    float dif = clamp(dot(n, l), 0., 1.);
    float d = RayMarch(p+n*SURF_DIST*2., l);
    if(d<length(lightPos-p)){
        dif *= .1;
    }
    return dif;
}

void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    vec3 col = vec3(0);
    vec3 ro = vec3(0.0, 1.5, 0.5);
    vec3 rd = normalize(vec3(uv.x, uv.y, 1));
    float d = RayMarch(ro, rd);
    vec3 p = ro + rd * d;
    float dif = GetLight(p);
    col = vec3(dif);
    col = pow(col, vec3(.4545));	// gamma correction
    col = gradientColor(length(col)).rgb;
    gl_FragColor = vec4(col, 1.);
}