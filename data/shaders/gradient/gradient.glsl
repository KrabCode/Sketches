uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

uniform int gradientType;
uniform int colorCount;
uniform vec4[100] colorValues;
uniform float[100] colorPositions;

vec3 rgb(in vec3 hsb){
    vec3 rgb = clamp(abs(mod(hsb.x*6.0+
    vec3(0.0, 4.0, 2.0), 6.0)-3.0)-1.0, 0.0, 1.0);
    rgb = rgb*rgb*(3.0-2.0*rgb);
    return hsb.z * mix(vec3(1.0), rgb, hsb.y);
}

float map(float value, float start1, float stop1, float start2, float stop2){
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}

float norm(float value, float start, float stop){
    return map(value, start, stop, 0., 1.);
}

vec4 standardLerp(vec4 colorA, vec4 colorB, float amt){
    return mix(colorA, colorB, amt);
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    float pos = uv.y;               // VERTICAL
    if (gradientType == 1) {        // HORIZONTAL
        pos = uv.x;
    } else if (gradientType == 2) { // CIRCULAR
        pos = map(length(cv), 0., length(vec2(0.5)), 0., 1.);
    }
    pos = clamp(pos, 0., 1.);

    int leftIndex = 0;
    int rightIndex = 1;
    for(int i = 0; i < 100; i++){
        if(pos > colorPositions[i] && pos < colorPositions[i+1]){
            leftIndex = i;
            rightIndex = i+1;
            break;
        }
        if(i > colorCount){
            break;
        }
    }

    vec4 colorA = vec4(rgb(colorValues[leftIndex].xyz), colorValues[leftIndex].a);
    vec4 colorB = vec4(rgb(colorValues[rightIndex].xyz), colorValues[rightIndex].a);
    float posA = colorPositions[leftIndex];
    float posB = colorPositions[rightIndex];
    float normalizedPosBetweenNeighbours = norm(pos, posA, posB);
    vec4 mixedColor = standardLerp(colorA, colorB, normalizedPosBetweenNeighbours);

    gl_FragColor = mixedColor;
}