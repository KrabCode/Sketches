uniform sampler2D texture;
uniform vec2 resolution;
uniform float innerEdge;
uniform float outerEdge;
uniform float intensity;
uniform float rotation;
uniform float steps;
uniform sampler2D multiplier;
uniform float spread;

mat2 rotate2d(float angle){
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;

    const float amount =  intensity * smoothstep(innerEdge, outerEdge, length(cv));

    vec3 rotationRGB = vec3(-spread, 0, spread) + rotation;

    const vec2 directionR = normalize(uv) * rotate2d(rotationRGB.r);
    const vec2 directionG = normalize(uv) * rotate2d(rotationRGB.g);
    const vec2 directionB = normalize(uv) * rotate2d(rotationRGB.b);

    const vec3 multiplierRGB = texture(multiplier, vec2(0.5)).rgb;
    vec2 offsetR = vec2(0);
    vec2 offsetG = vec2(0);
    vec2 offsetB = vec2(0);
    vec3 col = vec3(0);
    float stepSize = (amount / steps);
    for(float i = 0.; i < steps; i++){
        offsetR += directionR * stepSize;
        offsetG += directionG * stepSize;
        offsetB += directionB * stepSize;
        col.r += texture(texture, uv + offsetR).r;
        col.g += texture(texture, uv + offsetG).g;
        col.b += texture(texture, uv + offsetB).b;
    }
    col /= steps;
    col *= multiplierRGB;
    gl_FragColor = vec4(col, 1.0);
}
