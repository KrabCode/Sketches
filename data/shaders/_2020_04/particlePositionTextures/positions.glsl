uniform sampler2D texture;
uniform sampler2D particleSpeeds;
uniform vec2 resolution;
uniform float time;
uniform int particleCount;

void main(){
    float i = gl_FragCoord.x / particleCount.x;
    ivec2 posCoord = ivec2(i, 0);
    vec4 particlePos = texelFetch(texture, posCoord, 0);
    vec4 particleSpd = texelFetch(particleSpeeds, posCoord, 0);
    float x = particlePos.x + (-1*2*particleSpd.x);
    float y = particlePos.y + (-1*2*particleSpd.y);
    float z = 0;
    vec3 pos = vec3(x,y,z);
    gl_FragColor = vec4(pos, 1.);
}