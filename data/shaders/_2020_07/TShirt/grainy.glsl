uniform sampler2D texture;
uniform sampler2D gradient1;
uniform sampler2D gradient2;
uniform sampler2D gradient3;
uniform sampler2D image;
uniform vec2 resolution;
uniform float time;
uniform vec3 imagePos;
uniform vec3 imageScale;
uniform vec3 imageIntensityBounds;
uniform float fbmStrength;
uniform float qStrength;
uniform float rStrength;
uniform float fbmScale;
uniform float constant;
uniform int borderShape;
uniform float borderSize;
uniform float borderTransition;
uniform float innerEdge;
uniform float outerEdge;
uniform float intensity;

vec4 permute(vec4 x){ return mod(((x*34.0)+1.0)*x, 289.0); }
float permute(float x){ return floor(mod(((x*34.0)+1.0)*x, 289.0)); }
vec4 taylorInvSqrt(vec4 r){ return 1.79284291400159 - 0.85373472095314 * r; }
float taylorInvSqrt(float r){ return 1.79284291400159 - 0.85373472095314 * r; }
vec4 grad4(float j, vec4 ip){
    const vec4 ones = vec4(1.0, 1.0, 1.0, -1.0);
    vec4 p, s;

    p.xyz = floor(fract (vec3(j) * ip.xyz) * 7.0) * ip.z - 1.0;
    p.w = 1.5 - dot(abs(p.xyz), ones.xyz);
    s = vec4(lessThan(p, vec4(0.0)));
    p.xyz = p.xyz + (s.xyz*2.0 - 1.0) * s.www;

    return p;
}
float snoise(vec4 v){
    const vec2  C = vec2(0.138196601125010504, // (5 - sqrt(5))/20  G4
    0.309016994374947451);// (sqrt(5) - 1)/4   F4
    // First corner
    vec4 i  = floor(v + dot(v, C.yyyy));
    vec4 x0 = v -   i + dot(i, C.xxxx);

    // Other corners

    // Rank sorting originally contributed by Bill Licea-Kane, AMD (formerly ATI)
    vec4 i0;

    vec3 isX = step(x0.yzw, x0.xxx);
    vec3 isYZ = step(x0.zww, x0.yyz);
    //  i0.x = dot( isX, vec3( 1.0 ) );
    i0.x = isX.x + isX.y + isX.z;
    i0.yzw = 1.0 - isX;

    //  i0.y += dot( isYZ.xy, vec2( 1.0 ) );
    i0.y += isYZ.x + isYZ.y;
    i0.zw += 1.0 - isYZ.xy;

    i0.z += isYZ.z;
    i0.w += 1.0 - isYZ.z;

    // i0 now contains the unique values 0,1,2,3 in each channel
    vec4 i3 = clamp(i0, 0.0, 1.0);
    vec4 i2 = clamp(i0-1.0, 0.0, 1.0);
    vec4 i1 = clamp(i0-2.0, 0.0, 1.0);

    //  x0 = x0 - 0.0 + 0.0 * C
    vec4 x1 = x0 - i1 + 1.0 * C.xxxx;
    vec4 x2 = x0 - i2 + 2.0 * C.xxxx;
    vec4 x3 = x0 - i3 + 3.0 * C.xxxx;
    vec4 x4 = x0 - 1.0 + 4.0 * C.xxxx;

    // Permutations
    i = mod(i, 289.0);
    float j0 = permute(permute(permute(permute(i.w) + i.z) + i.y) + i.x);
    vec4 j1 = permute(permute(permute(permute (
    i.w + vec4(i1.w, i2.w, i3.w, 1.0))
    + i.z + vec4(i1.z, i2.z, i3.z, 1.0))
    + i.y + vec4(i1.y, i2.y, i3.y, 1.0))
    + i.x + vec4(i1.x, i2.x, i3.x, 1.0));
    // Gradients
    // ( 7*7*6 points uniformly over a cube, mapped onto a 4-octahedron.)
    // 7*7*6 = 294, which is close to the ring size 17*17 = 289.

    vec4 ip = vec4(1.0/294.0, 1.0/49.0, 1.0/7.0, 0.0);

    vec4 p0 = grad4(j0, ip);
    vec4 p1 = grad4(j1.x, ip);
    vec4 p2 = grad4(j1.y, ip);
    vec4 p3 = grad4(j1.z, ip);
    vec4 p4 = grad4(j1.w, ip);

    // Normalise gradients
    vec4 norm = taylorInvSqrt(vec4(dot(p0, p0), dot(p1, p1), dot(p2, p2), dot(p3, p3)));
    p0 *= norm.x;
    p1 *= norm.y;
    p2 *= norm.z;
    p3 *= norm.w;
    p4 *= taylorInvSqrt(dot(p4, p4));

    // Mix contributions from the five corners
    vec3 m0 = max(0.6 - vec3(dot(x0, x0), dot(x1, x1), dot(x2, x2)), 0.0);
    vec2 m1 = max(0.6 - vec2(dot(x3, x3), dot(x4, x4)), 0.0);
    m0 = m0 * m0;
    m1 = m1 * m1;
    return 49.0 * (dot(m0*m0, vec3(dot(p0, x0), dot(p1, x1), dot(p2, x2)))
    + dot(m1*m1, vec2(dot(p3, x3), dot(p4, x4))));
}

// Hash without Sine
// MIT License...
/* Copyright (c)2014 David Hoskins.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.*/

//----------------------------------------------------------------------------------------
//  1 out, 1 in...
float hash11(float p)
{
    p = fract(p * .1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

//----------------------------------------------------------------------------------------
//  1 out, 2 in...
float hash12(vec2 p)
{
    vec3 p3  = fract(vec3(p.xyx) * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

//----------------------------------------------------------------------------------------
//  1 out, 3 in...
float hash13(vec3 p3)
{
    p3  = fract(p3 * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

//----------------------------------------------------------------------------------------
//  2 out, 1 in...
vec2 hash21(float p)
{
    vec3 p3 = fract(vec3(p) * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.xx+p3.yz)*p3.zy);

}

//----------------------------------------------------------------------------------------
///  2 out, 2 in...
vec2 hash22(vec2 p)
{
    vec3 p3 = fract(vec3(p.xyx) * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yzx+33.33);
    return fract((p3.xx+p3.yz)*p3.zy);

}

//----------------------------------------------------------------------------------------
///  2 out, 3 in...
vec2 hash23(vec3 p3)
{
    p3 = fract(p3 * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yzx+33.33);
    return fract((p3.xx+p3.yz)*p3.zy);
}

//----------------------------------------------------------------------------------------
//  3 out, 1 in...
vec3 hash31(float p)
{
    vec3 p3 = fract(vec3(p) * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yzx+33.33);
    return fract((p3.xxy+p3.yzz)*p3.zyx);
}


//----------------------------------------------------------------------------------------
///  3 out, 2 in...
vec3 hash32(vec2 p)
{
    vec3 p3 = fract(vec3(p.xyx) * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yxz+33.33);
    return fract((p3.xxy+p3.yzz)*p3.zyx);
}

//----------------------------------------------------------------------------------------
///  3 out, 3 in...
vec3 hash33(vec3 p3)
{
    p3 = fract(p3 * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yxz+33.33);
    return fract((p3.xxy + p3.yxx)*p3.zyx);

}

//----------------------------------------------------------------------------------------
// 4 out, 1 in...
vec4 hash41(float p)
{
    vec4 p4 = fract(vec4(p) * vec4(.1031, .1030, .0973, .1099));
    p4 += dot(p4, p4.wzxy+33.33);
    return fract((p4.xxyz+p4.yzzw)*p4.zywx);

}

//----------------------------------------------------------------------------------------
// 4 out, 2 in...
vec4 hash42(vec2 p)
{
    vec4 p4 = fract(vec4(p.xyxy) * vec4(.1031, .1030, .0973, .1099));
    p4 += dot(p4, p4.wzxy+33.33);
    return fract((p4.xxyz+p4.yzzw)*p4.zywx);

}

//----------------------------------------------------------------------------------------
// 4 out, 3 in...
vec4 hash43(vec3 p)
{
    vec4 p4 = fract(vec4(p.xyzx)  * vec4(.1031, .1030, .0973, .1099));
    p4 += dot(p4, p4.wzxy+33.33);
    return fract((p4.xxyz+p4.yzzw)*p4.zywx);
}

//----------------------------------------------------------------------------------------
// 4 out, 4 in...
vec4 hash44(vec4 p4)
{
    p4 = fract(p4  * vec4(.1031, .1030, .0973, .1099));
    p4 += dot(p4, p4.wzxy+33.33);
    return fract((p4.xxyz+p4.yzzw)*p4.zywx);
}


float fbm (vec4 p) {
    float sum = 0.;
    float amp = 1;
    float freq = 1;
    // Loop of octaves
    for (int i = 0; i < 4; i++) {
        sum += amp*snoise(p*freq);
        freq *= 2.;
        amp *= .5;
        p += vec4(3.123, 2.456, 1.121, 2.4545);
    }
    return sum;
}

float noise(vec2 p, vec2 t, float amp, float freq){
    return (amp*snoise(vec4(p*freq, t)));
}

float map(float value, float start1, float stop1, float start2, float stop2){
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}


// IQ noise
float hash(float n)
{
    return fract(sin(n)*43758.5453);
}

float iqNoise(vec3 x){
    // The noise function returns a value in the range -1.0f -> 1.0f
    vec3 p = floor(x);
    vec3 f = fract(x);
    f  = f*f*(3.0-2.0*f);
    float n = p.x + p.y*57.0 + 113.0*p.z;
    return mix(mix(mix(hash(n+0.0), hash(n+1.0), f.x),
    mix(hash(n+57.0), hash(n+58.0), f.x), f.y),
    mix(mix(hash(n+113.0), hash(n+114.0), f.x),
    mix(hash(n+170.0), hash(n+171.0), f.x), f.y), f.z);
}


float fbm(vec3 p){
    float sum = 0.;
    float freq = 1.;
    float amp = 0.5;
    for (int i = 0; i < 6; i++){
        sum += amp*(1-2*iqNoise(p*freq));
        freq *= 2.0;
        amp *= .5;
    }
    return sum;
}

float fbm(vec2 p){
    float sum = 0.;
    float freq = 1.;
    float amp = 0.5;
    for (int i = 0; i < 6; i++){
        sum += amp*(1-2*iqNoise(vec3(p*freq, 0.)));
        freq *= 2.0;
        amp *= .5;
    }
    return sum;
}

float pattern( in vec2 p, out vec2 q, out vec2 r ){

    q.x = fbm( p + vec2(5.0,0.0) );
    q.y = fbm( p + vec2(2,1.3));

    r.x = fbm( p + 40.0*q + vec2(1.7-time,9.2) );
    r.y = fbm( p + 10.0*q + vec2(1.3,2.8+time) );

    return fbm( p + 2.*r);
}

vec3 blur(sampler2D tex, vec2 uv){
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    float offset = 1. / resolution.x;
    offset  *= 1. + intensity*smoothstep(innerEdge, outerEdge, length(cv));
    // Grouping texcoord variables in order to make it work in the GMA 950. See post #13
    // in this thread:
    // http://www.idevgames.com/forums/thread-3467.html
    vec2 tc0 = uv + vec2(-offset, -offset);
    vec2 tc1 = uv + vec2(0.0, -offset);
    vec2 tc2 = uv + vec2(+offset, -offset);
    vec2 tc3 = uv + vec2(-offset, 0.0);
    vec2 tc4 = uv + vec2(0.0, 0.0);
    vec2 tc5 = uv + vec2(+offset, 0.0);
    vec2 tc6 = uv + vec2(-offset, +offset);
    vec2 tc7 = uv + vec2(0.0, +offset);
    vec2 tc8 = uv + vec2(+offset, +offset);

    vec4 col0 = texture2D(tex, tc0);
    vec4 col1 = texture2D(tex, tc1);
    vec4 col2 = texture2D(tex, tc2);
    vec4 col3 = texture2D(tex, tc3);
    vec4 col4 = texture2D(tex, tc4);
    vec4 col5 = texture2D(tex, tc5);
    vec4 col6 = texture2D(tex, tc6);
    vec4 col7 = texture2D(tex, tc7);
    vec4 col8 = texture2D(tex, tc8);

    vec4 sum = (1.0 * col0 + 2.0 * col1 + 1.0 * col2 +
    2.0 * col3 + 4.0 * col4 + 2.0 * col5 +
    1.0 * col6 + 2.0 * col7 + 1.0 * col8) / 16.0;
    return sum.rgb;
}

// https://www.iquilezles.org/www/articles/warp/warp.htm
void main(){
    vec2 uv = gl_FragCoord.xy /  resolution.xy;
//    uv *= 19./6.;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    vec2 uvStatic = uv;
    vec2 t = vec2(cos(time), sin(time));
    vec2 q, r;
    float fbm = 0.5+0.5*pattern(uv*fbmScale, q, r);
    float fNoise = fbm;
    float qNoise = length(q);
    float rNoise = length(r);
    float imageIntensity = length(blur(image, imagePos.xy+uvStatic*imageScale.xy).rgb);
//    imageIntensity = smoothstep(imageIntensityBounds.x, imageIntensityBounds.y, imageIntensity);
    vec4 clr = texture(gradient1, vec2(.5, constant+fbmStrength*fbm+q*qStrength+r*rStrength));
    clr = mix(clr, texture(gradient2, vec2(.5, constant+fbmStrength*fbm+q*qStrength+r*rStrength)), pow(1.-imageIntensity/3., 2.));
    float d;
    if(borderShape == 0){
         d= max(abs(cv.y), abs(cv.x));
    }else if(borderShape == 1){
        d = length(cv);
    }
    float border = smoothstep(borderSize-borderTransition, borderSize+borderTransition, d);
    vec4 borderTex = texture(gradient3, vec2(.5, constant+fbmStrength*fbm+q*qStrength+r*rStrength));
    clr = mix(clr, borderTex, border);
    gl_FragColor = clr;
}



