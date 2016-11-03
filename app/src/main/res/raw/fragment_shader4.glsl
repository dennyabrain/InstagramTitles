#extension GL_OES_EGL_image_external : require

precision highp float;
uniform samplerExternalOES sTexture;
varying vec2 vTextureCoord;

const highp vec3 W = vec3(0.2989, 0.5870, 0.1140);
uniform float param; // time
uniform float param2; // voice
uniform float filRad;
uniform int filSec;
uniform float mX;
uniform float mY;

float rng2(vec2 seed)
{
    return fract(sin(dot(seed * floor(param2 * 12.), vec2(127.1,311.7))) * 43758.5453123);
}

float rng(float seed)
{
    return rng2(vec2(seed, 1.0));
}

void render(){
    vec2 uv = vTextureCoord;

    vec2 blockS = floor(uv * vec2(24., 9.));
    vec2 blockL = floor(uv * vec2(8., 4.));

    float r = rng2(uv);
    vec3 noise = (vec3(r, 1. - r, r / 2. + 0.5) * 1.0 - 2.0) * 0.08;

    float lineNoise = pow(rng2(blockS), 8.0) * pow(rng2(blockL), 3.0) - pow(rng(7.2341), 17.0) * 2.;

    vec4 col1 = texture2D(sTexture, uv);
    vec4 col2 = texture2D(sTexture, uv + vec2(lineNoise * 0.05 * rng(5.0), 0));
    vec4 col3 = texture2D(sTexture, uv - vec2(lineNoise * 0.05 * rng(31.0), 0));

    gl_FragColor = vec4(vec3(col1.x, col2.y, col3.z) + noise, 1.0);
}

void main() {
    float one = pow(vTextureCoord.x-mX, 2.0);
    float two = pow(vTextureCoord.y-mY, 2.0);
    float three = pow(filRad, 2.0);
    if(filSec==1){
        if(one+two<three){
            render();
        }
    }else{
        if(one+two>three){
            render();
        }
    }
}