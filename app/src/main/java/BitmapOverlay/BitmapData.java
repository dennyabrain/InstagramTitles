package BitmapOverlay;

import javax.microedition.khronos.opengles.GL10;

import dennymades.space.StitchAndroid.Constants;
import static android.opengl.GLES20.*;
/**
 * Created by abrain on 9/13/16.
 */
public class BitmapData {
    private static final int POSITION_COMPONENT_COUNT=2;
    private static final int TEXTURE_COORDINATE_COMPONENT_COUNT=2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT+TEXTURE_COORDINATE_COMPONENT_COUNT)* Constants.BYTE_PER_FLOAT;

    private final VertexArray mVertexArray;

    private static final float[] VERTEX_DATA={
            -0.8f, -0.8f, 0.0f, 0.0f,
            0.8f, 0.8f, 1.0f, -1.0f,
            -0.8f, 0.8f, 0.0f, -1.0f,

            -0.8f, -0.8f, 0.0f, 0.0f,
            0.8f, -0.8f, 1.0f, 0.0f,
            0.8f, 0.8f, 1.0f, -1.0f
    };

    public BitmapData(){
        mVertexArray = new VertexArray(VERTEX_DATA);
    }

    //method to bind the vertex array to the shaderprograms
    public void bindData(BitmapTextureShader textureProgram){
        mVertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        mVertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATE_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw(){
        //added for transparency
        // http://stackoverflow.com/a/2363177/2767642
        glEnable(GL10.GL_BLEND);
        glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        glDrawArrays(GL_TRIANGLES, 0, 6);
    }
}
