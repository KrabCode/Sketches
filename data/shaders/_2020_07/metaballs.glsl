uniform sampler2D texture;
uniform sampler2D gradient;
uniform vec2 resolution;
uniform float time;
uniform vec2[1000] positions;
uniform int posCount;

#define pi 3.1415

vec4 rampColor(float pct){
    return texture2D(gradient, vec2(0.5, clamp(pct, 0., 1.)));
}

float hash(float i){
    return fract(sin(i * 323.121f) * 454.123f);
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    float closestDist = resolution.x+resolution.y;
    vec2 closestPos = resolution.xy;
    vec2 closestPos2 = resolution.xy;
    float sum = 0;
    for (int i = 0; i < 1000.; i++){
        if (i >= posCount || length(positions[i]) == 0){
            break;
        }
        vec2 normPos = positions[i].xy / resolution.xy;
        normPos.y = 1.-normPos.y;
        float dist = distance(uv, normPos);
        float inverseDist = 0.02/dist;
        float angle = atan(normPos.y-uv.y, normPos.x-uv.x);
        sum += inverseDist*cos(angle*10.+time*.2);
    }

    vec4 gradientColor = rampColor(sum);
    vec4 originalColor = texture2D(texture, uv) * 0.;
    gl_FragColor = vec4(originalColor.rgb+gradientColor.rgb, 1.);
}