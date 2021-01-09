uniform sampler2D texture;
uniform sampler2D fg;
uniform sampler2D gradient;
uniform vec2 resolution;
uniform float time;

#define pi 3.14159

vec4 gradientColor(float pct){
    return texture2D(gradient, vec2(0.5, clamp(pct, 0., 1.)));
}

vec3 antialiasForeground(sampler2D tex, vec2 uv){
    float pixelThird = (1./resolution.x)/3.;
    vec2 aa = vec2(-pixelThird, pixelThird);
    vec3 c1 = texture2D(tex, uv+aa.xx).rgb;
    vec3 c2 = texture2D(tex, uv+aa.xy).rgb;
    vec3 c3 = texture2D(tex, uv+aa.yx).rgb;
    vec3 c4 = texture2D(tex, uv+aa.yy).rgb;
    return (c1+c2+c3+c4) / 4.;
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    float angle = (atan(cv.y, cv.x)) / (pi * 2);
    angle = mod(angle, 1.);


    vec3 fgColor = antialiasForeground(fg, uv).rgb;

    vec3 col = gradientColor(angle).rgb;
    col +=  fgColor*-1.;
    gl_FragColor = vec4(col, 1.);
}