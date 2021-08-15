
uniform float lowBound, highBound;
uniform bool useBounds;
uniform bool black;
uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    uv.y = 1. - uv.y;
    vec4 c;
    c.rgb = texture(texture, uv).rgb;
    float luma = 0.2126*c.r + 0.7152*c.g + 0.0722*c.b;
    if(useBounds){
        c.a = smoothstep(lowBound, highBound, length(luma));
    }else if(black){
        c.a = smoothstep(0.6, 0.3, length(luma));
    }else{
        c.a = smoothstep(0.25, 0.4, length(luma));
    }
    gl_FragColor = c;
}