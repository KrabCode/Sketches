
uniform mat4 modelviewMatrix;
uniform mat4 transformMatrix;
uniform mat3 normalMatrix;

uniform int lightCount;
uniform vec4 lightPosition[8];
uniform vec3 lightNormal[8];
uniform vec3 lightAmbient[8];
uniform vec3 lightDiffuse[8];
uniform vec3 lightSpecular[8];
uniform vec3 lightFalloff[8];
uniform vec2 lightSpot[8];

uniform float time;

uniform float minRadius;
uniform float maxRadius;
uniform int colorCount;
uniform vec4 hsba_0;
uniform vec4 hsba_1;
uniform vec4 hsba_2;
uniform vec4 hsba_3;
uniform vec4 hsba_4;
uniform vec4 hsba_5;
uniform vec4 hsba_6;
uniform vec4 hsba_7;
uniform vec4 hsba_8;
uniform vec4 hsba_9;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;

attribute vec4 ambient;
attribute vec4 specular;
attribute vec4 emissive;
attribute float shininess;

varying vec4 vertColor;
varying vec4 backVertColor;

const float zero_float = 0.0;
const float one_float = 1.0;
const vec3 zero_vec3 = vec3(0);

float falloffFactor(vec3 lightPos, vec3 vertPos, vec3 coeff) {
    vec3 lpv = lightPos - vertPos;
    vec3 dist = vec3(one_float);
    dist.z = dot(lpv, lpv);
    dist.y = sqrt(dist.z);
    return one_float / dot(dist, coeff);
}

float spotFactor(vec3 lightPos, vec3 vertPos, vec3 lightNorm, float minCos, float spotExp) {
    vec3 lpv = normalize(lightPos - vertPos);
    vec3 nln = -one_float * lightNorm;
    float spotCos = dot(nln, lpv);
    return spotCos <= minCos ? zero_float : pow(spotCos, spotExp);
}

float lambertFactor(vec3 lightDir, vec3 vecNormal) {
    return max(zero_float, dot(lightDir, vecNormal));
}

float blinnPhongFactor(vec3 lightDir, vec3 vertPos, vec3 vecNormal, float shine) {
    vec3 np = normalize(vertPos);
    vec3 ldp = normalize(lightDir - np);
    return pow(max(zero_float, dot(ldp, vecNormal)), shine);
}


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


float fbm (vec4 p) {
    float sum = 0.;
    float amp = 1;
    float freq = 1;
    // Loop of octaves
    for (int i = 0; i < 4; i++) {
        sum += amp*snoise(p*freq);
        freq *= 2.5;
        amp *= .35;
        p += vec4(3.123, 2.456, 1.121, 2.4545);
    }
    return sum;
}

void main() {
    // Vertex in clip coordinates

    float n = fbm(vec4(50.+position.xyz * .008, time*.1));
    vec4 pos = position+.1*n;
    vec4 clr = clamp(color+n*2., 0, 1);
    gl_Position = transformMatrix * pos;

    // Vertex in eye coordinates
    vec3 ecVertex = vec3(modelviewMatrix * pos);

    // Normal vector in eye coordinates
    vec3 ecNormal = normalize(normalMatrix * normal);
    vec3 ecNormalInv = ecNormal * -one_float;

    // Light calculations
    vec3 totalAmbient = vec3(0, 0, 0);

    vec3 totalFrontDiffuse = vec3(0, 0, 0);
    vec3 totalFrontSpecular = vec3(0, 0, 0);

    vec3 totalBackDiffuse = vec3(0, 0, 0);
    vec3 totalBackSpecular = vec3(0, 0, 0);

    for (int i = 0; i < 8; i++) {
        if (lightCount == i) break;

        vec3 lightPos = lightPosition[i].xyz;
        bool isDir = lightPosition[i].w < one_float;
        float spotCos = lightSpot[i].x;
        float spotExp = lightSpot[i].y;

        vec3 lightDir;
        float falloff;
        float spotf;

        if (isDir) {
            falloff = one_float;
            lightDir = -one_float * lightNormal[i];
        } else {
            falloff = falloffFactor(lightPos, ecVertex, lightFalloff[i]);
            lightDir = normalize(lightPos - ecVertex);
        }

        spotf = spotExp > zero_float ? spotFactor(lightPos, ecVertex, lightNormal[i],
        spotCos, spotExp)
        : one_float;

        if (any(greaterThan(lightAmbient[i], zero_vec3))) {
            totalAmbient       += lightAmbient[i] * falloff;
        }

        if (any(greaterThan(lightDiffuse[i], zero_vec3))) {
            totalFrontDiffuse  += lightDiffuse[i] * falloff * spotf *
            lambertFactor(lightDir, ecNormal);
            totalBackDiffuse   += lightDiffuse[i] * falloff * spotf *
            lambertFactor(lightDir, ecNormalInv);
        }

        if (any(greaterThan(lightSpecular[i], zero_vec3))) {
            totalFrontSpecular += lightSpecular[i] * falloff * spotf *
            blinnPhongFactor(lightDir, ecVertex, ecNormal, shininess);
            totalBackSpecular  += lightSpecular[i] * falloff * spotf *
            blinnPhongFactor(lightDir, ecVertex, ecNormalInv, shininess);
        }
    }

    // Calculating final color as result of all lights (plus emissive term).
    // Transparency is determined exclusively by the diffuse component.
    vertColor =     vec4(totalAmbient, 0) * clr * specular +
    vec4(totalFrontDiffuse, 1) * clr * specular  +
    vec4(totalFrontSpecular, 0) * specular +
    vec4(emissive.rgb, 0);

    backVertColor = vec4(totalAmbient, 0) * clr +
    vec4(totalBackDiffuse, 1) * clr +
    vec4(totalBackSpecular, 0) * specular +
    vec4(emissive.rgb, 0);
}