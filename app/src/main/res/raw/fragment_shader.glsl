#extension GL_OES_EGL_image_external : require

precision highp float;
uniform samplerExternalOES sTexture;
varying vec2 vTextureCoord;

const highp vec3 W = vec3(0.2989, 0.5870, 0.1140);
uniform float param;

void main() {
    float stongth = sin(param) * 0.5 + 0.5;
    vec2 uv = vTextureCoord;
    float waveu = sin((uv.y + param) * 20.0) * 0.5 * 0.05 * stongth;
    float wavev = sin((uv.x + param) * 20.0) * 0.5 * 0.05 * stongth;
    gl_FragColor = texture2D(sTexture, uv + vec2(waveu, wavev));
    //gl_FragColor= vec4(stongth, 0.5f, 0.2f, 1.0f);
}

