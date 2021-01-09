uniform sampler2D texture;
uniform sampler2D fluid;
uniform vec2 resolution;

vec4 lookup(vec2 coord){
    return texture2D(fluid,(coord)/resolution.xy);
}

void main(){
    gl_FragColor = lookup(gl_FragCoord.xy*.8).wwww;
}