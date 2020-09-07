uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

// Based on Ray Marching for Dummies!"
// by Martijn Steinrucken aka BigWings/CountFrolic - 2018
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

#define MAX_STEPS 100
#define MAX_DIST 100.
#define SURF_DIST .001
#define NORM_DIST 0.015
#define pi 3.1415

uniform sampler2D gradient;

vec4 gradientColor(float pct){
    return texture2D(gradient, vec2(0.5, clamp(pct, 0., 1.)));
}

vec3 repeat(vec3 p, float c){
    return p;
    //    return mod(p+0.5*c,c)-0.5*c;
}

float cubicPulse(float c, float w, float x){
    x = abs(x - c);
    if (x>w) return 0.0;
    x /= w;
    return 1.0 - x*x*(3.0-2.0*x);
}

float GetDist(vec3 p) {
    p.y += .025*cubicPulse(fract(p.z+time/(pi*2.)), 0.5, 0.5);
    p.y += .01*sin(p.x*3.+p.z*2.7);
    return p.y;
}

float RayMarch(vec3 ro, vec3 rd) {
    float dO = 0.;
    for (int i = 0; i<MAX_STEPS; i++) {
        vec3 p = ro + rd*dO;
        float dS = GetDist(p);
        dO += dS;
        if (dO>MAX_DIST || dS<SURF_DIST){
            break;
        }
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
    vec3 lightPos = vec3(0, 2.8, 0);
    //    lightPos.xz += vec2(sin(time), cos(time))*10.;
    vec3 l = normalize(lightPos-p);
    vec3 n = GetNormal(p);
    float dif = clamp(dot(n, l), 0., 1.0);
    float d = RayMarch(p+n*SURF_DIST*2., l);
    if (d < length(lightPos-p)){
        dif *= .25;
    }
    return dif;
}

float render(vec2 uv){
    vec3 ro = vec3(0.0, 0.5, 1.);
    vec3 rd = normalize(vec3(uv.x, uv.y, 1.));
    float d = RayMarch(ro, rd);
    vec3 p = ro + rd * d;
    return GetLight(p);
}

float aaRender(vec2 uv){
    float pixelThird = (1./resolution.x)/3.;
    vec2 aa = vec2(-pixelThird, pixelThird);
    float c1 = render(uv+aa.xx);
    float c2 = render(uv+aa.xy);
    float c3 = render(uv+aa.yx);
    float c4 = render(uv+aa.yy);
    return (c1+c2+c3+c4) / 4.;
}

void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    if (uv.y > 0){
        uv.y = -uv.y;
    }
    vec3 col = vec3(0);
    float dif = aaRender(uv);
    dif = smoothstep(0., 1.0, dif);
    dif = pow(dif, 1.);
    col = gradientColor(dif).rgb;
    gl_FragColor = vec4(col, 1.);
}