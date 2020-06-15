#define pi 3.14159


uniform sampler2D texture;
uniform vec2 resolution;
uniform float innerEdge;
uniform float outerEdge;
uniform float intensity;
uniform float rotation;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;
    float amount = intensity * 0.001 * smoothstep(innerEdge, outerEdge, length(cv));
    float angle = atan(cv.y, cv.x)+rotation;
    vec3 col;
    col.r = texture(texture, uv + vec2(amount*cos(angle), amount*sin(angle))).r;
    col.g = texture(texture, uv).g;
    col.b = texture(texture, uv + vec2(-amount*cos(angle), -amount*sin(angle))).b;

    col *= (1.0 - amount * 0.5);

    gl_FragColor = vec4(col, 1.0);
}
