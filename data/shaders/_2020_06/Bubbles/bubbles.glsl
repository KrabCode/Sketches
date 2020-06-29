uniform sampler2D gradient;
uniform vec2 resolution;
uniform float time;
uniform float alpha;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    gl_FragColor = vec4(texture(gradient,uv).rgb, alpha);
}