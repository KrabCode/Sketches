uniform sampler2D texture;
uniform sampler2D img_0;
uniform sampler2D img_1;
uniform sampler2D img_2;
uniform sampler2D img_3;
uniform sampler2D img_4;
uniform vec2 resolution;
uniform float time;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    uv.y = 1.-uv.y;
    vec3 i0 = texture(img_0, uv).rgb;
    vec3 i1 = texture(img_1, uv).rgb;
    vec3 i2 = texture(img_2, uv).rgb;
    vec3 i3 = texture(img_3, uv).rgb;
    vec3 i4 = texture(img_4, uv).rgb;

    vec3 col;

    col = mix(
        mix(i1, i0, smoothstep(0.7, 1.0, length(i0))),
         i3,
        smoothstep(1.2, 1.0, length(i2)));

    gl_FragColor = vec4(col, 1.);
}