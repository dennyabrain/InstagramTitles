
#extension GL_OES_EGL_image_external : require

precision mediump float;
uniform samplerExternalOES sTexture;
varying vec2 vTextureCoord;

const highp vec3 W = vec3(0.2989, 0.5870, 0.1140);
uniform float param;

float wave(float x, float amount) {
  return (sin(x * param));
}


void main() {
lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);
gl_FragColor = vec4(wave(textureColor.x, 10.0f), textureColor.y, textureColor.z, 1.0f);
}

