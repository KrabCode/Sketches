uniform sampler2D texture;
uniform sampler2D img_0;
uniform sampler2D img_1;
uniform sampler2D img_2;
uniform sampler2D img_3;
uniform sampler2D img_4;
uniform vec3 pos_0;
uniform vec3 pos_1;
uniform vec3 pos_2;
uniform vec3 pos_3;
uniform vec3 pos_4;
uniform vec3 size_0;
uniform vec3 size_1;
uniform vec3 size_2;
uniform vec3 size_3;
uniform vec3 size_4;

uniform vec2 resolution;
uniform float time;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    uv.y = 1.-uv.y;
    vec3 i0 = texture(img_0, size_0.xy*(uv+pos_0.xy*.0001)).rgb;
    vec3 i1 = texture(img_1, size_1.xy*(uv+pos_1.xy*.0001)).rgb;
    vec3 i2 = texture(img_2, size_2.xy*(uv+pos_2.xy*.0001)).rgb;
    vec3 i3 = texture(img_3, size_3.xy*(uv+pos_3.xy*.0001)).rgb;
    vec3 i4 = texture(img_4, size_4.xy*(uv+pos_4.xy*.0001)).rgb;

    vec3 col;
    col = i0.rgb;

    gl_FragColor = vec4(col, 1.);
}
