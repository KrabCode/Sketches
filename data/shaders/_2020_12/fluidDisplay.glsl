uniform sampler2D texture;
uniform sampler2D fluid;
uniform vec2 resolution;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec4 col = vec4(texture(fluid, uv).w);
    gl_FragColor = col;
}