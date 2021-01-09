// wyattflanders.com/MeAndMyNeighborhood.pdf

precision highp float;
precision highp sampler2D;


uniform sampler2D texture;
uniform vec2 resolution;
uniform float time;
uniform vec3 mouse;

vec4 lookup(vec2 coord){
    return texelFetch(texture, ivec2(coord), 0);
//    return texture(texture, vec2(coord / resolution.xy), 0);
}

vec4 field (vec2 position) {
    // Rule 1 : All My Energy translates with my ordered Energy
    vec2 velocityGuess = lookup(position).xy;
    vec2 positionGuess = position - velocityGuess;
    return lookup(positionGuess);
}

void main(){
    vec2 me = gl_FragCoord.xy;

    vec4 energy = field(me);

    // neighbours
    vec4 pX  =  field(me + vec2(1,0));
    vec4 pY  =  field(me + vec2(0,1));
    vec4 nX  =  field(me - vec2(1,0));
    vec4 nY  =  field(me - vec2(0,1));

    // Rule 2 : Disordered Energy diffuses completely :
    energy.b = (pX.b + pY.b + nX.b + nY.b)/4.0;


    // Rule 3 : Order in the disordered Energy creates Order :
    vec2 force;
    force.x = nX.b - pX.b;
    force.y = nY.b - pY.b;
    energy.xy += force/4.0;

    // Rule 4 : Disorder in the ordered Energy creates Disorder :
    energy.b += (nX.x - pX.x + nY.y - pY.y)/4.;

    // Gravity effect :
    energy.x += energy.w / 100.0;

    // Mass concervation :
    energy.w += (nX.x*nX.w-pX.x*pX.w+nY.y*nY.w-pY.y*pY.w)/4.;


    //Boundary conditions :
//    if(me.x<10.||me.y<10.||resolution.x-me.x<10.||resolution.y-me.y<10.)
//    {
//        energy.xy *= 0.;
//    }

    vec2 mouseFixed = mouse.xy;
    mouseFixed.y = resolution.y-mouseFixed.y;
    if(mouse.z > 0. && distance(me, mouseFixed) < 15.){
        energy.w = 1.0;
    }
    gl_FragColor = energy;
}