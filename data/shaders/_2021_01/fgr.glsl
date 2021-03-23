uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;


const int colorsPerGradient = 4;
const float pi = 3.14159;
const float tau = pi * 2.;

//--------------------------------------GRADIENT-----------------------------------
//--------------------------------color point array logic--------------------------
//-------------------------------------by Krabcode---------------------------------

struct colorPoint
{
    float pos;
    vec3 val;
};

colorPoint emptyColorPoint()
{
    return colorPoint(1.1, vec3(1.,0.,0.));
}

float map(float value, float start1, float stop1, float start2, float stop2)
{
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}

float norm(float value, float start, float stop)
{
    return map(value, start, stop, 0., 1.);
}

int findClosestLeftNeighbourIndex(float pos, colorPoint[colorsPerGradient] gradient)
{
    for(int i = 0; i < colorsPerGradient - 1; i++){
        if(pos >= gradient[i].pos){
            if(pos <= gradient[i+1].pos){
                return i;
            }
        }
    }
    return 0;
}

vec3 gradientColorAt(float normalizedPos, colorPoint[colorsPerGradient] gradient)
{
    float pos = clamp(normalizedPos, 0., 1.);
    int leftIndex = findClosestLeftNeighbourIndex(pos, gradient);
    int rightIndex = leftIndex + 1;
    colorPoint A = gradient[leftIndex];
    colorPoint B = gradient[rightIndex];
    float normalizedPosBetweenNeighbours = norm(pos, A.pos, B.pos);
    return mix(A.val, B.val, normalizedPosBetweenNeighbours);
}

vec3 hexToRgb(int color)
{
    float rValue = float(color / 256 / 256);
    float gValue = float(color / 256 - int(rValue * 256.0));
    float bValue = float(color - int(rValue * 256.0 * 256.0) - int(gValue * 256.0));
    return vec3(rValue / 255.0, gValue / 255.0, bValue / 255.0);
}

vec3 gammaCorrection(vec3 rgb){
    return pow(smoothstep(0., 1., rgb), vec3(1.0/2.2));
}

vec4 gammaCorrection(vec4 rgba){
    return vec4(gammaCorrection(rgba.rgb), 1.);
}

float sdBox( in vec2 p, in vec2 q )
{
    p = abs(p) - q;
    return smoothstep(0.001, 0.0, max(p.y,p.x));
}

float maxFromArray(float[9] arr){
    float runningSum = 0.;
    for(int i = 0; i < arr.length; i++){
        runningSum = max(runningSum, arr[i]);
    }
    return runningSum;
}

float sdBoxNeighborAware(vec2 p, vec2 q){
    float[9] neighbourhood = float[](
        sdBox(p,q),
        sdBox(p+vec2(1, 0),q),
        sdBox(p+vec2(1, 1),q),
        sdBox(p+vec2(0, 1),q),
        sdBox(p+vec2(-1, 1),q),
        sdBox(p+vec2(-1, 0),q),
        sdBox(p+vec2(-1, -1),q),
        sdBox(p+vec2(0, -1),q),
        sdBox(p+vec2(1, -1),q)
    );
    return maxFromArray(neighbourhood);
}


float ease(float p, float g) {
    if (p < 0.5) return 0.5f * pow(2 * p, g);
    return 1 - 0.5f * pow(2 * (1 - p), g);
}

mat2 rotate(float angle){
    return mat2(sin(angle), cos(angle), -cos(angle), sin(angle));
}

void main(){
    colorPoint[colorsPerGradient] gradient = colorPoint[](
        colorPoint(0.,  hexToRgb(0x2a3d66) * 0.5),
        colorPoint(0.25,hexToRgb(0x5d54a4)),
        colorPoint(0.75, hexToRgb(0x9d65c9)),
        colorPoint(1.0,hexToRgb(0xd789d7)));

    vec2 uv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    vec2 gv;
    float fig;

    float t = mod(time * .5, pi);
    vec2 size = vec2(0.25);
    if(t < pi * .5){
        gv = fract(uv * 5.) - .5;
        gv *= rotate(0.5*pi*ease(map(t, 0., pi*.5, 0., 1.), 2.));
        fig = sdBoxNeighborAware(gv, size);
    }else {
        gv = fract(0.5 + uv * 5.) - .5;
        gv *= rotate(.5*pi+0.5*pi*ease(map(t, pi*.5, pi, 0., 1.), 2.));
        fig = 1.-max(sdBox(gv, vec2(0.5-size.x, 0.75)), sdBox(gv, vec2(0.75, 0.5-size.y)));
    }

    vec3 col = gradientColorAt(fig, gradient);
    gl_FragColor = vec4(col, 1.);
}

















