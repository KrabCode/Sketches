
uniform sampler2D texture;
uniform vec2 resolution;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec4 col;
    col.rgb = texture2D(texture, uv).rgb;
    col.a = smoothstep(1.25, 1.5, length(col.rgb));
    gl_FragColor = col;
}