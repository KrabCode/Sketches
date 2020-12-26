uniform sampler2D texture;
uniform sampler2D img_0;
uniform sampler2D img_1;
uniform sampler2D img_2;
uniform sampler2D img_3;
uniform sampler2D img_4;

uniform vec2 resolution;
uniform float time;

vec3 power(vec3 p, float q){
    return vec3(pow(p.x, q),
    pow(p.y, q),
    pow(p.z, q)
    );
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;

    vec3 i0 = texture(img_0, uv).rgb;
    vec3 i1 = texture(img_1, uv).rgb;
    vec3 i2 = texture(img_2, uv).rgb;
    vec3 i3 = texture(img_3, uv).rgb;
    vec3 i4 = texture(img_4, uv).rgb;

    vec3 col = i3;

    col = mix(i2, i1, step(length(i3), 1.2));


    gl_FragColor = vec4(col, 1.);
}
