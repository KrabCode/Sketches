uniform sampler2D texture;
uniform sampler2D particlePositions;
uniform vec2 resolution;
uniform vec2 sketchResolution;
uniform float time;
uniform vec2 mousePos;
uniform int particleCount;

void main(){
    vec2 mouse = mousePos.xy / sketchResolution.xy;
//    mouse.y = 1-mouse.y;
    float i = gl_FragCoord.x / particleCount.x;
    ivec2 posCoord = ivec2(i, 0);
    vec4 particlePos = texelFetch(particlePositions, posCoord, 0);
    vec4 particleSpd = texelFetch(texture, posCoord, 0);
    vec2 toMouse = normalize(mouse-particlePos.xy);
    particleSpd.xy += toMouse*.5;
    float x = .5+.5*particleSpd.x;
    float y = .5+.5*particleSpd.y;
    float z = 0;
    vec3 pos = vec3(x,y,z);
    gl_FragColor = vec4(pos, 1.);
}