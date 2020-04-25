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
attribute vec4 color;
attribute vec2 offset;

varying vec4 vertColor;

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

const float tau = 6.28318530718;

vec3 rgb(in vec3 hsb){
    vec3 rgb = clamp(abs(mod(hsb.x*6.0+
    vec3(0.0, 4.0, 2.0), 6.0)-3.0)-1.0, 0.0, 1.0);
    rgb = rgb*rgb*(3.0-2.0*rgb);
    return hsb.z * mix(vec3(1.0), rgb, hsb.y);
}

float map(float value, float start1, float stop1, float start2, float stop2){
    return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
}

vec4 getColor(float pct){
    pct = clamp(pct, 0, 1);
    pct = fract(pct);
    float colorPct = clamp(map(pct, 0, 1, 0, colorCount-1), 0, colorCount-1);
    int previousColorIndex = int(floor(colorPct));
    float lerpToNextColor = fract(colorPct);
    vec4[] colors = vec4[](
    hsba_0, hsba_1, hsba_2, hsba_3, hsba_4, hsba_5, hsba_6, hsba_7, hsba_8, hsba_9);
    vec4 prevColor = colors[previousColorIndex];
    vec4 nextColor = colors[previousColorIndex+1];
    prevColor.rgb = rgb(prevColor.rgb);
    nextColor.rgb = rgb(nextColor.rgb);
    return mix(prevColor, nextColor, lerpToNextColor);
}

float angularDiameter(float r, float size) {
    return atan(size /  r);
}

float hash(float i){
    return fract(sin(i * 323.121f) * 454.123f);
}

void main() {
    float i = position.x;

    float r = i*0.008;
    float a = i*0.05-r+tau*.28;


    vec3 p = vec3(r*cos(a), r*sin(a), 0);

    vec4 clr = getColor(hash(i*.0000001+.01*time*.001));
    clr.a = 1.;

    vec4 pos = modelviewMatrix * vec4(p, 1);
    vec4 clip = projectionMatrix * pos;
    vec2 perspScale = (projectionMatrix * vec4(1, -1, 0, 0)).xy;
    vec2 noPerspScale = clip.w / (0.5 * viewport.zw);
    gl_Position.xy = clip.xy + offset.xy * mix(noPerspScale, perspScale, float(perspective > 0));
    gl_Position.zw = clip.zw;
    vertColor = clr;
}
