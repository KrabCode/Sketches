uniform sampler2D texture;
uniform sampler2D ramp;
uniform sampler2D img;
uniform vec2 resolution;
uniform float time;


vec4 rampColor(float pct){
    return texture2D(ramp, vec2(0.5, clamp(pct, 0., 1.)));
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    float d = length(cv);
    vec4 col = rampColor(d);
    vec4 boy = texture(img, uv+vec2(0.01,.05));
    col = min(col, boy);
    gl_FragColor = col;
}