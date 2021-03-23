uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

const int colorsPerGradient = 5;

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
        if(pos >= gradient[i].pos && pos <= gradient[i+1].pos){
            return i;
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

/*
colorPoint[colorsPerGradient] gradient = colorPoint[](
        colorPoint(0.0, hexToRgb(0x1b262c)*.5),
        colorPoint(0.2, hexToRgb(0x1b262c)),
        colorPoint(0.4, hexToRgb(0x0f4c75)),
        colorPoint(0.6, hexToRgb(0x3282b8)),
        colorPoint(0.8,hexToRgb(0xbbe1fa)),
        colorPoint(1.0, hexToRgb(0xbbe1fa)));
*/

mat2 rotate2d(float angle){
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}

float sdEquilateralTriangle( in vec2 p )
{
    const float k = sqrt(3.0);
    p.x = abs(p.x) - 1.0;
    p.y = p.y + 1.0/k;
    if( p.x+k*p.y>0.0 ) p = vec2(p.x-k*p.y,-k*p.x-p.y)/2.0;
    p.x -= clamp( p.x, -2.0, 0.0 );
    return -length(p)*sign(p.y);
}

float sdBox( in vec2 p, in vec2 b )
{
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

void main(){
    colorPoint[colorsPerGradient] gradient = colorPoint[](
        colorPoint(0.0, hexToRgb(0x231709)),
        colorPoint(0.5, hexToRgb(0xb68973)),
        colorPoint(0.9, hexToRgb(0xeabf9f)),
        colorPoint(1.,  hexToRgb(0xfaf3e0)),
        emptyColorPoint());

    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    cv *= 1.5;
    cv *= rotate2d(time-10.*length(cv));

    float d = smoothstep(0., -0.5, sdEquilateralTriangle(cv*0.8));
//    float d = smoothstep(0., -0.5, sdBox(cv, vec2(0.5)));

    vec3 col = gradientColorAt(d, gradient);
    gl_FragColor = vec4(col, 1.);
}