
uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    uv.y = 1. - uv.y;
    vec4 col;
    col.rgb = texture(texture, uv).rgb;
    float br = length(col);
    col.a = smoothstep(1.0, 0.999, br);
    gl_FragColor = col;
}