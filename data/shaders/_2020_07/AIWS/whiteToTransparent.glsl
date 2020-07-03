uniform sampler2D texture;
uniform sampler2D gradient;
uniform vec2 resolution;

vec4 gradientColor(float pct){
   return texture2D(gradient, vec2(0.5, clamp(pct, 0., 1.)));
}

void main() {
   vec2 uv = gl_FragCoord.xy / resolution.xy;
   vec4 clr = texture(texture, uv);
   clr.a = 1.-(clr.r + clr.g + clr.b) / 3.;
   clr.rgb = gradientColor(uv.y).rgb;
   gl_FragColor = clr;
}
