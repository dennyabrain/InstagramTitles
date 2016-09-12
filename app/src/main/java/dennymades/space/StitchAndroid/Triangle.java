package dennymades.space.StitchAndroid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by abrain on 9/9/16.
 */
public class Triangle {
    private FloatBuffer vertexBuffer;

    static final int COORDINATES_PER_VERTEX=3;
    static float[] coordTriangles= new float[9];

    float color[] = new float[4];

    private final String vertexShaderCode="" +
            "attribute vec4 vPosition;" +
            "void main(){" +
            "gl_Position = vPosition;" +
            "}";

    private final String fragmentShaderCode="" +
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main(){" +
            "gl_FragColor = vColor;" +
            "}";

    private int mProgram;

    private int mPositionHandle;
    private int mColorHandle;

    private int vertexCount = coordTriangles.length/COORDINATES_PER_VERTEX;
    private int vertexStride = COORDINATES_PER_VERTEX*4;

    public Triangle(float[] vertex, float[] paramColor){
        coordTriangles[0]=-vertex[0];
        coordTriangles[1]=-vertex[1];
        coordTriangles[2]=0;

        coordTriangles[3]=vertex[0];
        coordTriangles[4]=-vertex[1];
        coordTriangles[5]=0;

        coordTriangles[6]=0;
        coordTriangles[7]=vertex[2];
        coordTriangles[8]=0;

        color[0]=paramColor[0];
        color[1]=paramColor[1];
        color[2]=paramColor[2];
        color[3]=1.0f;

        ByteBuffer bb = ByteBuffer.allocateDirect(coordTriangles.length*4); //4 bytes in one float
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coordTriangles);
        vertexBuffer.position(0);

        int vertexShader = Shader.loadShader2(GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = Shader.loadShader2(GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = glCreateProgram();
        glAttachShader(mProgram, vertexShader);
        glAttachShader(mProgram, fragmentShader);
        glLinkProgram(mProgram);
    }

    public void draw(){
        glUseProgram(mProgram);
        mPositionHandle = glGetAttribLocation(mProgram, "vPosition");
        glEnableVertexAttribArray(mPositionHandle);
        glVertexAttribPointer(mPositionHandle,
                COORDINATES_PER_VERTEX,
                GL_FLOAT, false,
                vertexStride, vertexBuffer);
        mColorHandle = glGetUniformLocation(mProgram, "vColor");
        glUniform4fv(mColorHandle, 1, color, 0);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        glDisableVertexAttribArray(mPositionHandle);
    }

    public void reInitialize(float[] vertex, float[] paramColor){
        coordTriangles[0]=-vertex[0];
        coordTriangles[1]=-vertex[1];
        coordTriangles[2]=0;

        coordTriangles[3]=vertex[0];
        coordTriangles[4]=-vertex[1];
        coordTriangles[5]=0;

        coordTriangles[6]=0;
        coordTriangles[7]=vertex[2];
        coordTriangles[8]=0;

        color[0]=paramColor[0];
        color[1]=paramColor[1];
        color[2]=paramColor[2];
        color[3]=1.0f;

        ByteBuffer bb = ByteBuffer.allocateDirect(coordTriangles.length*4); //4 bytes in one float
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coordTriangles);
        vertexBuffer.position(0);
    }
}
