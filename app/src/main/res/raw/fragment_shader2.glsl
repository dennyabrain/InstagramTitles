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


void main() {
    float one = pow(vTextureCoord.x-mX, 2.0);
    float two = pow(vTextureCoord.y-mY, 2.0);
    float three = pow(filRad, 2.0);
    vec2 uv = vTextureCoord;

    if(filSec==1){
        if(one+two<three){
            float amount = 0.0;

            amount = (1.0 + sin(param2*6.0)) * 0.5;
            amount *= 1.0 + sin(param2*16.0) * 0.5;
            amount *= 1.0 + sin(param2*19.0) * 0.5;
            amount *= 1.0 + sin(param2*27.0) * 0.5;
            amount = pow(amount, 3.0);

            amount *= 0.05;

            vec3 col;

            col.r = texture2D( sTexture, vec2(uv.x+amount,uv.y) ).r;
            col.g = texture2D( sTexture, uv ).g;
            col.b = texture2D( sTexture, vec2(uv.x-amount,uv.y) ).b;

            col *= (1.0 - amount * 0.5);
            gl_FragColor = vec4(col, 1.0f);
        }
    }else{
        if(one+two>three){
            float amount = 0.0;

            amount = (1.0 + sin(param2*6.0)) * 0.5;
            amount *= 1.0 + sin(param2*16.0) * 0.5;
            amount *= 1.0 + sin(param2*19.0) * 0.5;
            amount *= 1.0 + sin(param2*27.0) * 0.5;
            amount = pow(amount, 3.0);

            amount *= 0.05;

            vec3 col;

            col.r = texture2D( sTexture, vec2(uv.x+amount,uv.y) ).r;
            col.g = texture2D( sTexture, uv ).g;
            col.b = texture2D( sTexture, vec2(uv.x-amount,uv.y) ).b;

            col *= (1.0 - amount * 0.5);
            gl_FragColor = vec4(col, 1.0f);
        }
    }
}
