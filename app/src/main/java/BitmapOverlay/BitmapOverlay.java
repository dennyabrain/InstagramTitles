package BitmapOverlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Size;

import java.io.File;

import dennymades.space.StitchAndroid.R;

import static android.opengl.GLES20.*;
/**
 * Created by abrain on 9/13/16.
 */
public class BitmapOverlay {
    Bitmap mBitmap;
    protected int textureId;
    protected String fileName;
    private int mWidth;
    private int mHeight;

    public BitmapOverlay(){

    }

    public BitmapOverlay(Bitmap bmp, int width, int height) throws Exception{
        mWidth = width;
        mHeight = height;
        Bitmap snugFit = overlayBitmap(bmp);

        int[] textures = new int[1];
        glGenTextures(1, textures, 0);
        if(textures[0]==0){
            throw new Exception("Error generating texture");
        }
        glBindTexture(GL_TEXTURE_2D, textures[0]);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, snugFit, 0);

        textureId=textures[0];
    }

    public int getTextureId(){
        return textureId;
    }

    public void loadBitmap(){
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String filePath = dir.getAbsolutePath()+"/Stitch/emoji.png";

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mBitmap = BitmapFactory.decodeFile(filePath, options);
        mBitmap.setHasAlpha(true);

        //mBitmap.eraseColor(Color.TRANSPARENT);
    }

    public Bitmap getBitmap(){
        return mBitmap;
    }

    private void init(){

    }

    public void drawBitmap(){

    }

    public void setStuff(Bitmap bmp) throws Exception{
        int[] textures = new int[1];
        glGenTextures(1, textures, 0);
        if(textures[0]==0){
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

    private Bitmap overlayBitmap(Bitmap bmp){
        Bitmap biggerBitmap = Bitmap.createBitmap(mHeight, mWidth, bmp.getConfig());
        Canvas cnvs = new Canvas(biggerBitmap);
        cnvs.drawBitmap(biggerBitmap, new android.graphics.Matrix(), null);

        Point midPointLarge = new Point(biggerBitmap.getWidth()/2, biggerBitmap.getHeight()/2);
        Point offsetSmall = new Point(bmp.getWidth()/2, bmp.getHeight()/2);
        Point topLeft = new Point(midPointLarge.x-offsetSmall.x, midPointLarge.y-offsetSmall.y);

        //cnvs.drawBitmap(bmp, new android.graphics.Matrix(), null);
        cnvs.drawBitmap(bmp, topLeft.x, topLeft.y, new Paint());
        return biggerBitmap;
    }
}
