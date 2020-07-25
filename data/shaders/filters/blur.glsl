#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
uniform vec2 resolution;
uniform float innerEdge;
uniform float outerEdge;
uniform float intensity;

// for easy copying purposes
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


void main(void) {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
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

    vec4 col0 = texture2D(texture, tc0);
    vec4 col1 = texture2D(texture, tc1);
    vec4 col2 = texture2D(texture, tc2);
    vec4 col3 = texture2D(texture, tc3);
    vec4 col4 = texture2D(texture, tc4);
    vec4 col5 = texture2D(texture, tc5);
    vec4 col6 = texture2D(texture, tc6);
    vec4 col7 = texture2D(texture, tc7);
    vec4 col8 = texture2D(texture, tc8);

    vec4 sum = (1.0 * col0 + 2.0 * col1 + 1.0 * col2 +
    2.0 * col3 + 4.0 * col4 + 2.0 * col5 +
    1.0 * col6 + 2.0 * col7 + 1.0 * col8) / 16.0;
    gl_FragColor = vec4(sum.rgb, 1.0);
}
