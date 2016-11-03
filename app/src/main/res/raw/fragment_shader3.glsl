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

#define steps 2.

void render(){
    float stongth = sin(param2) * 0.5 + 0.5;
    vec2 uv = vTextureCoord;
    float waveu = sin((uv.y + param2) * 20.0) * 0.5 * 0.05 * stongth;
    gl_FragColor = texture2D(sTexture, uv + vec2(waveu, 0));
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



