
uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;
uniform float pixelate;
uniform vec2 freq;
uniform vec2 offset;
uniform float whitePoint;
uniform float blackPoint;

vec4 hash44(vec4 p4)
{
    p4 = fract(p4  * vec4(.1031, .1030, .0973, .1099));
    p4 += dot(p4, p4.wzxy+33.33);
    return fract((p4.xxyz+p4.yzzw)*p4.zywx);
}

void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    uv += offset;
    uv = floor(uv * pixelate);
    float t = time;
    float tr = 2.5;
    vec4 p = vec4(uv*vec2(freq.x ,freq.y), t, 0.);
    float n = length(hash44(p));
    n = smoothstep(blackPoint, whitePoint, n);
    n = pow(n, 3.);
    vec3 col = vec3(n);
    gl_FragColor = vec4(col, 1.);
}