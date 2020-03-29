#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

vec2 g( vec2 n ) { return sin(n.x*n.y*vec2(12,17)+vec2(1,2)); }
//vec2 g( vec2 n ) { return sin(n.x*n.y+vec2(0,1.571)); } // if you want the gradients to lay on a circle

float noise(vec2 p){
    const float kF = 3.1415927;  // make 6 to see worms

    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f*f*(3.0-2.0*f);
    return mix(mix(sin(kF*dot(p,g(i+vec2(0,0)))),
    sin(kF*dot(p,g(i+vec2(1,0)))),f.x),
    mix(sin(kF*dot(p,g(i+vec2(0,1)))),
    sin(kF*dot(p,g(i+vec2(1,1)))),f.x),f.y);
}


void main(){
    vec2 uv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    vec3 col = vec3(.5+.5*noise(uv*30.));
    gl_FragColor = vec4(col, 1.);
}