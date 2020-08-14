uniform sampler2D texture;
uniform sampler2D grad;
uniform vec2 resolution;
uniform float time;

#define pi 3.1415

vec4 gradientColor(float pct){
    return texture2D(grad, vec2(0.5, clamp(pct, 0., 1.)));
}

void main(){
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    float atan = .5+.5*(atan(cv.y, cv.x) / pi);
    gl_FragColor = gradientColor(atan);
}