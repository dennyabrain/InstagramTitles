package BitmapOverlay;

import android.content.Context;
import android.util.Log;

import dennymades.space.StitchAndroid.R;
import dennymades.space.StitchAndroid.Shader;
import static android.opengl.GLES20.*;
/**
 * Created by abrain on 9/13/16.
 */
public class BitmapTextureShader extends Shader{
    private final String TAG = this.getClass().getName();
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String A_POSITION = "a_Position";
    protected static final String U_TEXTURE = "u_Texture";
    protected static final String A_TEXTURE_COORDINATES = "a_Texture_Coordinates";

    private final int uMatrixLocation;
    private final int aPositionLocation;
    private final int uTextureLocation;
    private final int aTextureCoordinatesLocation;

    protected BitmapTextureShader(Context context) throws Exception {
        /*shader = new Shader();
        try {
            shader.setProgram(vertexShaderId, fragmentShaderId, context);
            program = shader.programHandle();
        } catch (Exception e) {
            Log.d(TAG, "exception setting program", e);
        }*/
        super();
        setProgram(R.raw.bitmap_vertex_shader, R.raw.bitmap_fragment_shader, context);
        uMatrixLocation = getHandle("u_Matrix");
        aPositionLocation = getHandle("a_Position");
        uTextureLocation = getHandle("u_Texture");
        aTextureCoordinatesLocation = getHandle("a_Texture_Coordinates");
    }

    public void useProgram(){
        useProgram();
    }

    public void setUniforms(float[] matrix, int textureId){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureLocation, 0);
    }

    public int getPositionAttributeLocation(){
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation(){
        return aTextureCoordinatesLocation;
    }
}
