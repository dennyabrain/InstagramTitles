#extension GL_OES_EGL_image_external : require

precision highp float;
uniform samplerExternalOES sTexture;
varying vec2 vTextureCoord;

const highp vec3 W = vec3(0.2989, 0.5870, 0.1140);
uniform float param; // time
uniform float param2; // voice

void main() {
    float stongth = sin(param2) * 0.5 + 0.5;
    vec2 uv = vTextureCoord;
    float waveu = sin((uv.y + param2) * 20.0) * 0.5 * 0.05 * stongth;
    gl_FragColor = texture2D(sTexture, uv + vec2(waveu, 0));
}
