uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

#define pi acos(-1.)


// beautiful sdf visualisation from here: https://www.shadertoy.com/view/XsyGRW
vec3 draw_line(float d, float thickness) {
    const float aa = 3.0;
    return vec3(smoothstep(0.0, aa / resolution.y, max(0.0, abs(d) - thickness)));
}

vec3 draw_line(float d) {
    return draw_line(d, 0.005);
}

float draw_solid(float d) {
    return smoothstep(0.0, 3.0 / resolution.y, max(0.0, d));
}

vec3 draw_distance(float d, vec2 p) {
    float t = clamp(d * 0.85, 0.0, 1.0);
    vec3 grad = mix(vec3(1, 0.8, 0.5), vec3(0.3, 0.8, 1), t);
    float d0 = abs(1.0 - draw_line(mod(d + 0.1, 0.2) - 0.1).x);
    float d1 = abs(1.0 - draw_line(mod(d + 0.025, 0.05) - 0.025).x);
    float d2 = abs(1.0 - draw_line(d).x);
    vec3 rim = vec3(max(d2 * 0.85, max(d0 * 0.25, d1 * 0.06125)));
    grad -= rim;
    grad -= mix(vec3(0.05, 0.35, 0.35), vec3(0.0), draw_solid(d));
    return grad;
}

float map(float x, float a1, float a2, float b1, float b2){
    return b1 + (b2-b1) * (x-a1) / (a2-a1);
}

mat2 rotate2d(float angle){
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}


float sdTri( in vec2 p, in vec2 q ){
    p.x = abs(p.x);
    vec2 a = p - q*clamp( dot(p,q)/dot(q,q), 0.0, 1.0 );
    vec2 b = p - q*vec2( clamp( p.x/q.x, 0.0, 1.0 ), 1.0 );
    float s = -sign( q.y );
    vec2 d = min( vec2( dot(a,a), s*(p.x*q.y-p.y*q.x) ),
    vec2( dot(b,b), s*(p.y-q.y)  ));
    return -sqrt(d.x)*sign(d.y);
}
float sdBox( in vec2 p, in vec2 q )
{
    p = abs(p) - q;
    return max(p.y,p.x);
}
float sdBox( in vec4 p, in vec4 q )
{
    p = abs(p) - q;
    return max(p.y,max(p.x,max(p.z,p.w)));
}

void main(){
    float t = time*.02;
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    vec2 tw = vec2(cos(t), sin(t))*.5;
    float cd = length(cv);
    float a = atan(cv.y, cv.x);
    vec4 p = vec4(cv, 0, 0);
    p.xw *= rotate2d(t*2.+10.05);
    p.yw *= rotate2d(pi*sin(cd*10.)+t);
    p.yx *= rotate2d(10.*pi+cd+cos(a*8.));
    p.xz *= rotate2d(pi*t+2.8);
    float j = 0.2;
    p = mod(p,j) - 0.5*j;
    float sd = sdBox(p, vec4(0.05, 0.5, 0.5, 1.5));
//        vec3 color = draw_distance(sd, cv);
    vec3 color = 1.-draw_line(sd);
    vec4 orig = texture2D(texture, uv);
    gl_FragColor = vec4(orig.rgb+color.rgb*.25, 1.);
}
