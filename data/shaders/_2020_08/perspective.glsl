uniform sampler2D texture;
uniform sampler2D gradient;
uniform vec2 resolution;
uniform float time;
#define pi 3.1415

vec4 gradientColor(float pct){
    return texture2D(gradient, vec2(0.5, clamp(pct, 0., 1.)));
}

float cubicPulse(float c, float w, float x){
    x = abs(x - c);
    if (x>w) return 0.0;
    x /= w;
    return 1.0 - x*x*(3.0-2.0*x);
}

float sdfLine(vec2 uv, float blur){
    float w = 0.2;
    float horLine = cubicPulse(0.5, w, uv.x) ;
    float verLine = cubicPulse(0.5, w, uv.y) ;
    float line = max(horLine, verLine);
    return line + blur;
}

vec3 render(vec2 uv, float blur){
    float line = sdfLine(uv, blur);
    return gradientColor(line).rgb;
}

vec3 aaRender(vec2 uv, float blur){
    float pixelThird = (1./resolution.x)/3.0;
    vec2 aa = vec2(-pixelThird, pixelThird);
    vec3 c1 = render(uv+aa.xx, blur);
    vec3 c2 = render(uv+aa.xy, blur);
    vec3 c3 = render(uv+aa.yx, blur);
    vec3 c4 = render(uv+aa.yy, blur);
    return (c1+c2+c3+c4) / 4.;
}

vec3 perspective(vec2 uv, out float blur){
    float distanceFromPlane = 0.05;
    vec3 dir = vec3(uv, distanceFromPlane);
    if (dir.y != 0.0){
        dir /= abs(dir.y);
    }
    dir.z += time / (pi*.5);
    dir = fract(dir);
    blur = abs(uv.y - 0.5);
    return dir;
}

mat2 rotate2d(float angle){
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}

vec3 hash32(vec2 p)
{
    vec3 p3 = fract(vec3(p.xyx) * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yxz+33.33);
    return fract((p3.xxy+p3.yzz)*p3.zyx);
}


void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    float angle = cos(atan(cv.y, cv.x)*1.);
    cv *= rotate2d(angle*pi);
    float blur;
    vec3 dir = perspective(cv, blur);
    vec3 c = aaRender(dir.xz, blur);
//    c -= pow(1.-blur, 5.);
    c += vec3(.15*length(hash32(1000*uv.xy)));
    gl_FragColor = vec4(c, 1.);
}