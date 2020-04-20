/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-15 The Processing Foundation
  Copyright (c) 2004-12 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation, version 2.1.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

uniform mat4 projectionMatrix;
uniform mat4 modelviewMatrix;
 
uniform vec4 viewport;
uniform int perspective; 
uniform float time;

attribute vec4 position;
attribute vec2 offset;
attribute vec4 color;


varying vec4 vertColor;

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
  for (int i = 0; i < 2; i++) {
    sum += amp*snoise(p*freq);
    freq *= 2.;
    amp *= .5;
    p += vec4(3.123, 2.456, 1.121, 2.4545);
  }
  return sum;
}

float random(float seed) {
  return fract(sin(seed * 323.121f) * 454.123f);
}

vec4 galaxy(float i){

  float tr = 0.01;
  vec2 t = vec2(tr*cos(time), tr*sin(time));

  float e = 1.618282;
  float a = i;

  float theta = 0.23;
  float r = pow(e, a*theta);

  float armOffset = floor(random(i)*1);

  float finalAngle = armOffset+a-time/4.;

  float x = r*cos(finalAngle);
  float y = -40+.75*r*sin(finalAngle);
  float z = r*sin(finalAngle);

  x += .28*r*fbm(vec4(i*100,0, t.x, t.y));
  y += .28*r*fbm(vec4(100+i*100,0, t.x, t.y));
  z += .28*r*fbm(vec4(200+i*100,0, t.x, t.y));

  return vec4(x,y,z,1);
}

void main() {
  float i = position.x*(0.003);

  vec4 clr = color;
  clr.a *= random(i);

  vec4 myPos = galaxy(i);
  vec4 pos = modelviewMatrix * (position*vec4(0,1,1,1)+myPos);
  vec4 clip = projectionMatrix * pos;

  // Perspective ---
  // convert from world to clip by multiplying with projection scaling factor
  // invert Y, projections in Processing invert Y
  vec2 perspScale = (projectionMatrix * vec4(1, -1, 0, 0)).xy;

  // formula to convert from clip space (range -1..1) to screen space (range 0..[width or height])
  // screen_p = (p.xy/p.w + <1,1>) * 0.5 * viewport.zw

  // No Perspective ---
  // multiply by W (to cancel out division by W later in the pipeline) and
  // convert from screen to clip (derived from clip to screen above)
  vec2 noPerspScale = clip.w / (0.5 * viewport.zw);

  gl_Position.xy = clip.xy + offset.xy * mix(noPerspScale, perspScale, float(perspective > 0));
  gl_Position.zw = clip.zw;
  
  vertColor = clr;
}
