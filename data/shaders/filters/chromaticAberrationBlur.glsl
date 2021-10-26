uniform sampler2D texture;
uniform vec2 resolution;
uniform float innerEdge;
uniform float outerEdge;
uniform float intensity;
uniform float rotation;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 cv = (gl_FragCoord.xy-.5*resolution) / resolution.y;

    const float steps = 1.;
    const float amount =  intensity * 0.000001 * smoothstep(innerEdge, outerEdge, length(cv));

    const vec2 direction = normalize(uv);

    vec2 offs = vec2(0);
    vec3 col = vec3(0);

    for(float i = 0.; i < steps; i++){
        const vec2 chromabUv = uv + offs;
        offs += direction * (amount / steps);

        col.r += texture(texture, chromabUv + offs).r;
        col.g += texture(texture, chromabUv ).g;
        col.b += texture(texture, chromabUv - offs).b;
    }
    col /= steps;

    gl_FragColor = vec4(col, 1.0);
}