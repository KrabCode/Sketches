uniform sampler2D texture;
uniform sampler2D palette;
uniform vec2 resolution;
uniform float time;
uniform float detailBase;
uniform float detailRange;
uniform vec3 cPos;
uniform vec3 uvPos;
uniform float zoom;
uniform float cRange;


float map(float value, float start1, float stop1, float start2, float stop2){
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}

vec3 render(vec2 uv){
    uv = vec2(map(uv.x, -.5, .5, uvPos.x-zoom, uvPos.x+zoom),
    map(uv.y, -.5, .5, -uvPos.y-zoom, -uvPos.y+zoom));
    float t = time;
    vec2 c = vec2(cPos.x+cos(t)*cRange, cPos.y+sin(t)*cRange);
    vec2 z;
    z.x = 3.0 * uv.x;
    z.y = 2.0 * uv.y;
    float detail = detailBase + detailRange * cos(t);
    float i;
    for (i=0; i<detail; i++) {
        float x = (z.x * z.x - z.y * z.y) + c.x;
        float y = (z.y * z.x + z.x * z.y) + c.y;

        if ((x * x + y * y) > 4.0) break;
        z.x = x;
        z.y = y;
    }

    float pct = (i == detail ? 1.0 : float(i)) / detail;
    //    pct = pow(pct, 1.2);
    return texture(palette, vec2(0.5, pct)).rgb;
}

vec3 aaRender(vec2 uv){
    float pixelThird = (1./resolution.x)/3.0;
    vec2 aa = vec2(-pixelThird, pixelThird);
    vec3 c1 = render(uv+aa.xx);
    vec3 c2 = render(uv+aa.xy);
    vec3 c3 = render(uv+aa.yx);
    vec3 c4 = render(uv+aa.yy);
    return (c1+c2+c3+c4) / 4.;
}

void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    gl_FragColor = vec4(aaRender(uv), 1.);
}