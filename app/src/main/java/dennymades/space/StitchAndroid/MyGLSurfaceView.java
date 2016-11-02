package dennymades.space.StitchAndroid;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.hardware.Camera.Size;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import AudioRecorder.AudioRecorderHandlerThread;
import BitmapOverlay.BitmapData;
import BitmapOverlay.BitmapOverlay;
import BitmapOverlay.BitmapTextureShader;
import Encoder.TextureMovieEncoder;
import util.FileManager;
import util.Messages;
import util.TriangleHelper;

import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;

/**
 * Created by abrain on 9/8/16.
 */
public class MyGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private String TAG = this.getClass().getName();

    private Context mContext;

    private Handler mMainActivityCallback;

    /**
     * Camera and SurfaceTexture
     */
    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;

    //private final FBORenderTarget mRenderTarget = new FBORenderTarget();
    private final OESTexture mCameraTexture = new OESTexture();
    public static Shader[] mOffscreenShader;
    private int shaderIndex = 0;
    private int mWidth, mHeight;
    private boolean updateTexture = false;

    int[] a = new int[5];
    /**
     * OpenGL params
     */
    public static ByteBuffer mFullQuadVertices;
    public static float[] mTransformM = new float[16];
    public static float[] mOrientationM = new float[16];
    public static float[] mRatio = new float[2];

    //Grafika Renderer stuff
    private TextureMovieEncoder mVideoEncoder = new TextureMovieEncoder(this);
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

    int paramLocation;
    float param = 1.0f;
    int param2Location;
    float param2 = 1.0f;
    int dir = 1;
    long globalStartTime;
    private Calendar calendar;

    private float filterRadius=1.0f;
    private int filterRadiusLocation;
    private int filterSection;
    private float mX=0.5f, mY=0.5f;
    private int mXLocation, mYLocation;

    //Audio Encoder
    private AudioRecorderHandlerThread mAudioRecordHandlerThread;

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
        calendar = Calendar.getInstance();
    }

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture){
        updateTexture = true;
        requestRender();
    }


    @Override
    public synchronized void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //load and compile shader
        globalStartTime = System.nanoTime();
        mMainActivityCallback.sendMessage(Message.obtain(null, Messages.REQUEST_MUXER));
        Log.d("Denny", "SurfaceCreated");
        th = new TriangleHelper();
        mOffscreenShader = new Shader[6];
        try {
            mOffscreenShader[0] = new Shader();
            mOffscreenShader[0].setProgram(R.raw.vertex_shader, R.raw.fragment_shader, mContext);
            mOffscreenShader[1] = new Shader();
            mOffscreenShader[1].setProgram(R.raw.vertex_shader, R.raw.fragment_shader2, mContext);
            mOffscreenShader[2] = new Shader();
            mOffscreenShader[2].setProgram(R.raw.vertex_shader, R.raw.fragment_shader3, mContext);
            mOffscreenShader[3] = new Shader();
            mOffscreenShader[3].setProgram(R.raw.vertex_shader, R.raw.fragment_shader4, mContext);
            mOffscreenShader[4] = new Shader();
            mOffscreenShader[4].setProgram(R.raw.vertex_shader, R.raw.fragment_shader5, mContext);
            mOffscreenShader[5] = new Shader();
            mOffscreenShader[5].setProgram(R.raw.vertex_shader, R.raw.fragment_shader6, mContext);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
        mVideoEncoder.setTriangle(th);
        mBitmap = new BitmapData();
        try {
            mBitmapShader = new BitmapTextureShader(mContext);
        } catch (Exception e) {
            Log.d(TAG, "exception creating bitmap texture : ", e);
        }
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

        //draw frame content
        drawFrameContent();

        mVideoEncoder.setTextureId(mTextureId);
        if(beginRecording==true){
            mVideoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(mOutpuFile, camera_height, camera_width, 4607406, EGL14.eglGetCurrentContext()));
            beginRecording=false;
        }
    }

    public static void renderQuad(int aPosition){
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
        mAudioRecordHandlerThread.stopRecording();
    }

    public void startRecording(){
        Log.d(TAG, "in startRecording func");
        //mVideoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(mOutpuFile, 480, 640, 100000, EGL14.eglGetCurrentContext()));
        Log.d(TAG, "egl context : "+EGL14.eglGetCurrentContext().toString());
        beginRecording=true;
        mAudioRecordHandlerThread.startEncoding();
    }

    public void setBitmap(Bitmap bmp){
        try {
            bmpOverlay = new BitmapOverlay(bmp, camera_width, camera_height);
            mBmpTextureId = bmpOverlay.getTextureId();
        } catch (Exception e) {
            Log.d(TAG, "exception setting Bitmap", e);
        }
        showBitmap=true;
    }

    public void setBitmapShow(boolean flag){
        initBitmapShow=flag;
    }

    public void setAudioRecorderHandler(AudioRecorderHandlerThread arht){
        mAudioRecordHandlerThread = arht;
    }

    public void drawFrameContent(){
        glEnable(GL10.GL_BLEND);
        glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mOffscreenShader[shaderIndex].useProgram();

        int uTransformM = mOffscreenShader[shaderIndex].getHandle("uTransformM");
        int uOrientationM = mOffscreenShader[shaderIndex].getHandle("uOrientationM");
        int uRatioV = mOffscreenShader[shaderIndex].getHandle("ratios");
        paramLocation = mOffscreenShader[shaderIndex].getHandle("param");
        param2Location = mOffscreenShader[shaderIndex].getHandle("param2");
        filterRadiusLocation = mOffscreenShader[shaderIndex].getHandle("filRad");
        filterSection = mOffscreenShader[shaderIndex].getHandle("filSec");
        mXLocation = mOffscreenShader[shaderIndex].getHandle("mX");
        mYLocation = mOffscreenShader[shaderIndex].getHandle("mY");

        GLES20.glUniformMatrix4fv(uTransformM, 1, false, mTransformM, 0);
        GLES20.glUniformMatrix4fv(uOrientationM, 1, false, mOrientationM, 0);
        GLES20.glUniform2fv(uRatioV, 1, mRatio, 0);

        //mX = 1.0f; mY = 1.0f;

        filterRadius=filterRadius;
        GLES20.glUniform1f(mXLocation, mX);
        GLES20.glUniform1f(mYLocation, mY);

        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
        param = currentTime%100;
        //Log.d(TAG, "seconds : "+param);
        GLES20.glUniform1f(paramLocation, param);

        if(param2<4){
            param2 = 0.0f;
        }
        GLES20.glUniform1f(param2Location, param2);

        GLES20.glUniform1i(filterSection, 1);
        GLES20.glUniform1f(filterRadiusLocation, filterRadius);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//TADA  GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mCameraTexture.getTextureId());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);

        renderQuad(mOffscreenShader[shaderIndex].getHandle("aPosition"));

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
    }

    public void drawTransitionFrameContent(float x, float y, float r){
        //draw using previous program;
        //draw the region inside the circle


        //draw using current program;
        //draw the regin outside the circle
    }

    public void setCallback(Handler h){
        mMainActivityCallback = h;
    }

    public TextureMovieEncoder getTextureMovieEncoder(){
        return mVideoEncoder;
    }

    public void setParam(float v){
        param2 = v;
    }

    public void incrementShaderIndex(){
        shaderIndex=(shaderIndex+1)%6;
    }

    public void updateRadius(float r){
        filterRadius = r*2;
    }

    public void updateTouchCoordinates(float x, float y){
        mX = x/camera_width;
        mY = x/camera_height;
    }

}