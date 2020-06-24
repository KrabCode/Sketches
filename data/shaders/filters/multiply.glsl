uniform sampler2D texture;
uniform vec2 resolution;
uniform float amt;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec4 tex = texture(texture, uv);
    tex.rgb *= amt;
    gl_FragColor = tex;
}