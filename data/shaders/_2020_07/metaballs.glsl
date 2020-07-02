uniform sampler2D texture;
uniform sampler2D gradient;
uniform vec2 resolution;
uniform float time;
uniform vec2[1000] positions;
uniform int posCount;

vec4 rampColor(float pct){
    return texture2D(gradient, vec2(0.5, clamp(pct, 0., 1.)));
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    float closestDist = resolution.x+resolution.y;
    vec2 closestPos = resolution.xy;
    for(int i = 0; i < 1000.; i++){
        if(i >= posCount || length(positions[i]) == 0){
            break;
        }
        vec2 normPos = positions[i].xy / resolution.xy;
        normPos.y = 1.-normPos.y;
        float dist = distance(uv, normPos);
        if(dist < closestDist){
            closestDist = dist;
            closestPos = normPos;
        }
    }
//    float a = atan(closestPos.y-uv.y, closestPos.x-uv.x);
    vec4 gradientColor = rampColor(smoothstep(0.0, .02, closestDist));
    vec4 originalColor = texture2D(texture, uv);
    gl_FragColor = vec4(originalColor.rgb+gradientColor.rgb, 1.);
}