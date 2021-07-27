
uniform bool keepBlack;
uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    uv.y = 1. - uv.y;
    vec4 c;
    c.rgb = texture(texture, uv).rgb;
    float luma = 0.2126*c.r + 0.7152*c.g + 0.0722*c.b;
    if(keepBlack){
        c.a = smoothstep(0.6, 0.0, length(luma));
    }else{
        c.a = smoothstep(0.2, 0.6, length(luma));
    }
    gl_FragColor = c;
}