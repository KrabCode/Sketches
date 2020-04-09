
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying vec4 vertColor;
varying vec4 backVertColor;

void main() {
    vec4 color = gl_FrontFacing ? vertColor : backVertColor;
//    float exp = 1.0;
//    color.r = pow(color.r, exp);
//    color.g = pow(color.g, exp);
//    color.b = pow(color.b, exp);
    gl_FragColor = color;
}