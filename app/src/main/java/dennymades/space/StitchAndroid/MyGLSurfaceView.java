package dennymades.space.StitchAndroid;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.hardware.Camera.Size;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import BitmapOverlay.BitmapData;
import BitmapOverlay.BitmapOverlay;
import BitmapOverlay.BitmapTextureShader;
import Encoder.TextureMovieEncoder;
import util.FileManager;
import util.TriangleHelper;

/**
 * Created by abrain on 9/8/16.
 */
public class MyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private String TAG = this.getClass().getName();

    private Context mContext;

    /**
     * Camera and SurfaceTexture
     */
    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;

    //private final FBORenderTarget mRenderTarget = new FBORenderTarget();
    private final OESTexture mCameraTexture = new OESTexture();
    private final Shader mOffscreenShader = new Shader();
    private int mWidth, mHeight;
    private boolean updateTexture = false;

    /**
     * OpenGL params
     */
    private ByteBuffer mFullQuadVertices;
    private float[] mTransformM = new float[16];
    public static float[] mOrientationM = new float[16];
    private float[] mRatio = new float[2];

    //Grafika Renderer stuff
    private static TextureMovieEncoder mVideoEncoder = new TextureMovieEncoder();
    private static File mOutpuFile;
    private int mTextureId;
    private boolean beginRecording =false;
    int camera_width;
    int camera_height;

    //Overlay Stuff
    private TriangleHelper th;

    //Bitmap Overlay
    public static BitmapData mBitmap;
    public static BitmapTextureShader mBitmapShader;
    private Bitmap emoji;
    public static BitmapOverlay bmpOverlay;
    public static int mBmpTextureId;
    public boolean showBitmap=false;
    public boolean initBitmapShow=false;

    public MyGLSurfaceView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init(){
        //Create full scene quad buffer
        final byte FULL_QUAD_COORDS[] = {-1, 1, -1, -1, 1, 1, 1, -1};
        mFullQuadVertices = ByteBuffer.allocateDirect(4 * 2);
        mFullQuadVertices.put(FULL_QUAD_COORDS).position(0);

        setPreserveEGLContextOnPause(true);
        setEGLConfigChooser(false);
        setEGLContextClientVersion(2);
        setRenderer(this);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        //OutpuFile
        mOutpuFile = FileManager.getOutputMediaFile(2);
    }

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture){
        updateTexture = true;
        requestRender();
    }


    @Override
    public synchronized void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //load and compile shader
        Log.d("Denny", "SurfaceCreated");
        th = new TriangleHelper();
        try {
            mOffscreenShader.setProgram(R.raw.vertex_shader, R.raw.fragment_shader, mContext);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
        mVideoEncoder.setTriangle(th);
        mBitmap = new BitmapData();
        try {
            mBitmapShader = new BitmapTextureShader(mContext);
        } catch (Exception e) {
            Log.d(TAG, "exception creating bitmap texture : ", e);
        }

        /*emoji = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.emoji);
        if()
        try {
            //bmpOverlay = new BitmapOverlay(emoji);
            bmpOverlay = new BitmapOverlay();
            bmpOverlay.loadBitmap();
            Bitmap temp = bmpOverlay.getBitmap();
            bmpOverlay.setStuff(temp);
            mBmpTextureId = bmpOverlay.getTextureId();
        } catch (Exception e) {
            Log.d(TAG, "exception binding bitmap to texture", e);
        }*/
    }

    @SuppressLint("NewApi")
    @Override
    public synchronized void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport( 0, 0, width, height );
        mWidth = width;
        mHeight= height;

        //generate camera texture------------------------
        mCameraTexture.init();

        //set up surfacetexture------------------
        mTextureId=mCameraTexture.getTextureId();
        mSurfaceTexture = new SurfaceTexture(mTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);


        //set camera para-----------------------------------
        camera_width =0;
        camera_height =0;

        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        mCamera = Camera.open(0);
        try{
            mCamera.stopPreview();
            mCamera.setPreviewTexture(mSurfaceTexture);
        }catch(IOException ioe){
            Log.d(TAG, "IO Exception in setting preview texture", ioe);
        }

        Camera.Parameters param = mCamera.getParameters();
        List<Size> psize = param.getSupportedPreviewSizes();
        if(psize.size() > 0 ){
            int i;
            for (i = 0; i < psize.size(); i++){
                if(psize.get(i).width < width || psize.get(i).height < height)
                    break;
            }
            if(i>0)
                i--;
            param.setPreviewSize(psize.get(i).width, psize.get(i).height);

            camera_width = psize.get(i).width;
            camera_height= psize.get(i).height;

        }

        //get the camera orientation and display dimension------------
        if(mContext.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT){
            Matrix.setRotateM(mOrientationM, 0, 90.0f, 0f, 0f, 1f);
            mRatio[1] = camera_width*1.0f/height;
            mRatio[0] = camera_height*1.0f/width;
        }
        else{
            Matrix.setRotateM(mOrientationM, 0, 0.0f, 0f, 0f, 1f);
            mRatio[1] = camera_height*1.0f/height;
            mRatio[0] = camera_width*1.0f/width;
        }

        //start camera-----------------------------------------
        mCamera.setParameters(param);
        //TODO replace with better camera orientation detection method.
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();

        //start render---------------------
        requestRender();

        //Encoder stuff
        //mVideoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(mOutpuFile, 480, 640, 100000, EGL14.eglGetCurrentContext()));
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        synchronized (this){
            if(updateTexture){
                mSurfaceTexture.updateTexImage();
                mVideoEncoder.frameAvailable(mSurfaceTexture);
                mSurfaceTexture.getTransformMatrix(mTransformM);
                updateTexture=false;
            }
        }

        //render the texture to FBO if new frame is available
        //GLES20.glViewport(0, 0, mWidth, mHeight);

        mOffscreenShader.useProgram();

        int uTransformM = mOffscreenShader.getHandle("uTransformM");
        int uOrientationM = mOffscreenShader.getHandle("uOrientationM");
        int uRatioV = mOffscreenShader.getHandle("ratios");

        GLES20.glUniformMatrix4fv(uTransformM, 1, false, mTransformM, 0);
        GLES20.glUniformMatrix4fv(uOrientationM, 1, false, mOrientationM, 0);
        GLES20.glUniform2fv(uRatioV, 1, mRatio, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//TADA        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mCameraTexture.getTextureId());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);

        renderQuad(mOffscreenShader.getHandle("aPosition"));

        //th.drawTriangle();
        GLES20.glFlush();

        if(initBitmapShow==true){
            setBitmap(MainActivity.mEmojiTextBitmap);
            initBitmapShow=false;
        }

        if(showBitmap==true) {
            mBitmapShader.useTheProgram();
            mBitmapShader.setUniforms(mOrientationM, mBmpTextureId);
            mBitmap.bindData(mBitmapShader);
            mBitmap.draw();
        }


        mVideoEncoder.setTextureId(mTextureId);
        if(beginRecording==true){
            mVideoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(mOutpuFile, camera_height, camera_width, 4607406, EGL14.eglGetCurrentContext()));
            beginRecording=false;
        }
    }

    private void renderQuad(int aPosition){
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_BYTE, false, 0, mFullQuadVertices);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onDestroy(){
        updateTexture = false;
        mSurfaceTexture.release();
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
        }

        mCamera = null;
    }

    public void stopRecording(){
        Log.d(TAG, "in stopRecording func");
        mVideoEncoder.stopRecording();
    }

    public void startRecording(){
        Log.d(TAG, "in startRecording func");
        //mVideoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(mOutpuFile, 480, 640, 100000, EGL14.eglGetCurrentContext()));
        Log.d(TAG, "egl context : "+EGL14.eglGetCurrentContext().toString());
        beginRecording=true;
    }

    public void setBitmap(Bitmap bmp){
        try {
            bmpOverlay = new BitmapOverlay(bmp, camera_width, camera_height);
            //bmpOverlay.loadBitmap();
            //Bitmap temp = bmpOverlay.getBitmap();
            //bmpOverlay.setStuff(temp);
            mBmpTextureId = bmpOverlay.getTextureId();
        } catch (Exception e) {
            Log.d(TAG, "exception setting Bitmap", e);
        }
        showBitmap=true;
    }

    public void setBitmapShow(boolean flag){
        initBitmapShow=flag;
    }

}
