package BitmapOverlay;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import static android.opengl.GLES20.*;
/**
 * Created by abrain on 9/13/16.
 */
public class BitmapOverlay {
    Bitmap mBitmap;
    protected int textureId;
    protected String fileName;

    public BitmapOverlay(){

    }

    public BitmapOverlay(Bitmap bmp) throws Exception{
        int[] textures = new int[1];
        glGenTextures(GL_TEXTURE_2D, textures, 0);
        if(textures[0]==-1){
            throw new Exception("Error generating texture");
        }
        glBindTexture(GL_TEXTURE_2D, textures[0]);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0);

        textureId=textures[0];
    }

    public int getTextureId(){
        return textureId;
    }

    public void LoadBitmap(){

    }

    private void init(){

    }

    public void drawBitmap(){

    }
}
