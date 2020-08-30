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
    float pixelThird = (1./resolution.x)/3.;
    vec2 aa = vec2(-pixelThird, pixelThird);
    vec3 c1 = render(uv+aa.xx, blur);
    vec3 c2 = render(uv+aa.xy, blur);
    vec3 c3 = render(uv+aa.yx, blur);
    vec3 c4 = render(uv+aa.yy, blur);
    return (c1+c2+c3+c4) / 4.;
}

vec3 perspective(vec2 uv, out float blur){
    float distanceFromPlane = 0.25;
    vec3 dir = vec3(uv - 0.5, distanceFromPlane);
    if (dir.y != 0.0){
        dir /= abs(dir.y);
    }
    dir.z += time / pi;
    dir = mod(dir, 1.);
    blur = abs(uv.y - 0.5);
    return dir;
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    float blur;
    vec3 dir = perspective(uv, blur);
    vec3 c = aaRender(dir.xz, blur);
    c -= pow(1.-blur, 5.);
    gl_FragColor = vec4(c, 1.);
}