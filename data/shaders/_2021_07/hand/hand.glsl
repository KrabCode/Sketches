
uniform sampler2D texture;
uniform sampler2D img;
uniform vec2 resolution;
uniform float time;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec4 col;
    col.rgb = texture(img, uv).rgb;
    float br = length(col);
    col.a = smoothstep(1.0, 0.999, br);
    gl_FragColor = col;
}