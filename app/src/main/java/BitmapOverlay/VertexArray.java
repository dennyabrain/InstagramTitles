package BitmapOverlay;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import dennymades.space.StitchAndroid.Constants;

import static android.opengl.GLES20.*;
/**
 * Created by abrain on 9/13/16.
 */
public class VertexArray {
    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData){
        floatBuffer = ByteBuffer
                .allocateDirect(vertexData.length* Constants.BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }

    public void setVertexAttribPointer(int dataOffset, int attribLocation, int componentCount, int stride){
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attribLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attribLocation);

        floatBuffer.position(0);
    }
}
