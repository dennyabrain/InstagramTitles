#extension GL_OES_EGL_image_external : require

precision highp float;
uniform samplerExternalOES sTexture;
varying vec2 vTextureCoord;

const highp vec3 W = vec3(0.2989, 0.5870, 0.1140);
uniform float param; // time
uniform float param2; // voice

#define steps 2.

void main() {
    vec2 uv = vTextureCoord;

    vec4 c = texture2D(sTexture,uv);
    float g = max(c.r,max(c.g,c.b))*steps;
    float fuck = 345.678+10.0;
    float f = mod((uv.x+uv.y+500.)*sin(param2),1.);
    if(mod(g,1.0)>f)
        c.r = ceil(g);
    else
        c.r = floor(g);
    c.r/=steps;

    gl_FragColor = vec4(c.r, c.r, c.r, c.a);
}