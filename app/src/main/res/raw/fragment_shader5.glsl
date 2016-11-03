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

void render(){
    vec2 pixel = vTextureCoord;

    vec2 adjustedPixel = pixel;
    const float intensity = .1;

    adjustedPixel.y = 0.0;
    adjustedPixel.x = pixel.x;

    vec4 noiseTexture = texture2D( sTexture,adjustedPixel);
    vec4 soundTexture = texture2D( sTexture,adjustedPixel);

    pixel.x += sin(noiseTexture.x * param2) * intensity - .05;
    vec3 videoTexture = texture2D(sTexture, pixel).xyz;
    videoTexture.r -= 1.0 - ((sin(param2 * noiseTexture.y) + 1.0) / 2.);

    videoTexture.g = .2;
    videoTexture.b = .2;

    gl_FragColor = vec4(videoTexture, 1.0);
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



