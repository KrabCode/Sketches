uniform sampler2D gradient;
uniform vec2 resolution;

const float tau = 6.28318;

void main(){
    vec2 uv = (gl_FragCoord.xy - .5 * resolution.xy) / resolution.y;
    float theta = atan(uv.y, uv.x);
    float rotated = theta + tau * 0.25;
    float normalized = fract(rotated / tau);
    float flipped = 1.-normalized;
    gl_FragColor = texture2D(gradient, vec2(0.5, flipped));
}