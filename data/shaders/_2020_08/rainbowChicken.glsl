uniform sampler2D texture;
uniform sampler2D gradient;
uniform vec2 resolution;
uniform float time;


vec4 rampColor(float pct){
    return texture2D(gradient, vec2(0.5, clamp(pct, 0., 1.)));
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution.xy) / resolution.y;
    vec4 chickColor = texture(texture, uv).rgba;
    vec3 gradientColor = rampColor(mod(length(cv)*2.-time*.25, 1.)).rgb;
    chickColor.rgb *= gradientColor.rgb;
    gl_FragColor = chickColor;
}