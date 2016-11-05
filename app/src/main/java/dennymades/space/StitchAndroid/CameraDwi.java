package dennymades.space.StitchAndroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

import util.Permission;

/**
 * Created by abrain on 10/20/16.
 */
public class CameraDwi {
    private String TAG = this.getClass().getName();
    private CameraManager manager;
    private CameraDevice cameraDevice;
    private String FRONT_FACING;
    private String REAR_FACING;
    private Size imageDimensionFront;
    private Size imageDimensionBack;
    private String currentCamera;

    private int version = 0;

    private CaptureRequest.Builder captureRequestBuilder;

    private CameraCaptureSession mcameraCaptureSession;

    private Handler mBackgroundHandler;

    private HandlerThread mBackgroundThread;

    private SurfaceTexture mSurfaceTexture;

    /* Camera Object for Old APIS */
    private Camera oldCamera;

    /* Store the id of front old camera*/
    private int oldFrontId;

    /* Store the id of rear old camera*/
    private int oldRearId;

    /* flag to store current active old camera. 0 = uninitialized compare to oldFrontId or oldRearId */
    private int oldCameraIdFlag = -1;

    /* store size of front old camera; */
    private Size oldCameraFrontSize;

    /* store size of rear old camera */
    private Size oldCameraRearSize;

    public CameraDwi(){
        //detect API level and setup version of camera
        /*int currentAPI = Build.VERSION.SDK_INT;
        if(currentAPI>= Build.VERSION_CODES.LOLLIPOP){
            version=2;
        }else{
            version=1;
        }*/
        version=2;
        Log.d(TAG, "version chosen : "+version);

    }

    /********************************************************************************************
    * open fetches a list of all available cameras on the device and assigns                     *
    * front facing and back facing camera ids to FRONT_FACING and REAR_FACING respectively.      *
    * it then checks if the necessary camera permissions exist and asks the CameraManager to     *
    * open the current camera (it defaults to REAR_FACING)                                       *
     */

    public void open(){
        if(version==1){
            if(oldCameraIdFlag!=-1){
                //open old camera and set dimension
                setDimensionsAndOldCamera(oldCameraIdFlag);
            }else{
                //todo. move this to constructor;
                Camera.CameraInfo info = new Camera.CameraInfo();
                int numOfCameras = Camera.getNumberOfCameras();
                Log.d(TAG, "number of cameras for old API "+numOfCameras);
                for(int i=0;i<numOfCameras;i++){
                    Camera.getCameraInfo(i, info);
                    if(info.facing== Camera.CameraInfo.CAMERA_FACING_BACK){
                        oldRearId = i;
                    }
                    if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                        oldFrontId = i;
                        oldCameraIdFlag = i;
                        try{
                            setDimensionsAndOldCamera(i);
                        }catch(RuntimeException e){
                            Log.d(TAG, "exception opening old camera", e);
                        }
                    }
                }
            }
            // for old camera you don't need call back to create preview. So
            // I am doing that within open itself.
            try {
                oldCamera.setPreviewTexture(mSurfaceTexture);
            } catch (IOException e) {
                Log.d(TAG, "error setting preview texture in old camera ", e);
            }
            oldCamera.setDisplayOrientation(90);
            oldCamera.startPreview();

        }else if(version==2){
            manager = (CameraManager)MainActivity.context.getSystemService(Context.CAMERA_SERVICE);
            setDimensionsAndCameras();
            try {
                if(Permission.checkPermission(MainActivity.context, MainActivity.permissions)==false){
                    Permission.seekPermission((Activity) MainActivity.context, MainActivity.permissions, Permission.PERMISSION_ALL);
                }
                manager.openCamera(currentCamera, stateCallBack, mBackgroundHandler);
            } catch (CameraAccessException e) {
                Log.d(TAG, "get Camera Id access exception", e);
            }
        }
    }

    private final CameraDevice.StateCallback stateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.d(TAG, "camera opened");
            cameraDevice = camera;

            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {

        }

        @Override
        public void onError(CameraDevice cameraDevice, int i) {

        }
    };

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureBufferLost(CameraCaptureSession session, CaptureRequest request, Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }

        @Override
        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
        }

        @Override
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }
    };

    private CameraCaptureSession.StateCallback captureStateCallback = new CameraCaptureSession.StateCallback(){
        @Override
        public void onActive(CameraCaptureSession session) {
            super.onActive(session);
        }

        @Override
        public void onClosed(CameraCaptureSession session) {
            super.onClosed(session);
        }

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            Log.d(TAG, "capture session configured");
            mcameraCaptureSession = cameraCaptureSession;
            updatePreview();
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            Toast.makeText((Activity)MainActivity.context, "capture request configuration failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReady(CameraCaptureSession session) {
            super.onReady(session);
            Log.d(TAG, "capture session ready");
        }

        @Override
        public void onSurfacePrepared(CameraCaptureSession session, Surface surface) {
            super.onSurfacePrepared(session, surface);
            Log.d(TAG, "preview surface prepared");
        }
    };

    /*
    * Parses through the available output sizes and returns the first one
    * todo : we go through the list of sizes and choose one better suited to the device's resolution.
    */

    private Size getOptimizedSize(CameraCharacteristics characteristics){
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        assert map!=null;
        Size[] sizes = map.getOutputSizes(SurfaceTexture.class);

        for(Size size : sizes){
            Log.d(TAG, "supported size  - "+size.getHeight()+","+size.getWidth());
        }

        return sizes[0];
    }

    private void createCameraPreview(){
        try{
            //SurfaceTexture texture = MainActivity.textureView.getSurfaceTexture();
            SurfaceTexture texture = mSurfaceTexture;
            assert texture!=null;
            if(currentCamera==FRONT_FACING){
                texture.setDefaultBufferSize(imageDimensionFront.getWidth(), imageDimensionFront.getHeight());
            }else if(currentCamera==REAR_FACING){
                texture.setDefaultBufferSize(imageDimensionBack.getWidth(), imageDimensionBack.getHeight());
            }
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), captureStateCallback, null);
        }catch(Exception e){
            Log.d(TAG, "exception is creating camera preview");
        }
    }

    private void updatePreview(){
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try{
            mcameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        }catch(Exception e){
            Log.d(TAG, "problem submitting capture request", e);
        }
    }

    public void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    public void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void swapCamera(){
        if(version==1){
            oldCamera.stopPreview();
            oldCamera.release();
            //oldCamera = null;
            if(oldCameraIdFlag==oldFrontId){
                //FRONT
                oldCameraIdFlag=oldRearId;
            }else if(oldCameraIdFlag==oldRearId){
                //REAR
                oldCameraIdFlag=oldFrontId;
            }
            open();
        }else if(version ==2){
            cameraDevice.close();
            cameraDevice = null;
            if(currentCamera==FRONT_FACING){
                currentCamera = REAR_FACING;
            }else if(currentCamera==REAR_FACING){
                currentCamera = FRONT_FACING;
            }else{
                return;
            }
            open();
        }
    }

    public void setSurfaceTexture(SurfaceTexture st){
        Log.d(TAG, "surface texture set");
        mSurfaceTexture = st;
    }

    public Size getDimension(){
        if(version==1){
            if(oldCameraIdFlag==1){
                //front
                return oldCameraFrontSize;
            }else if(oldCameraIdFlag==2){
                //rear
                return oldCameraRearSize;
            }

        }else if(version==2){
            if(currentCamera == FRONT_FACING){
                return imageDimensionFront;
            }else{
                return imageDimensionBack;
            }
        }
        return null;
    }

    /*
    * Set the camera types and their respective dimensions
     */
    public void setDimensionsAndCameras(){
        String []cameraid = new String[0];
        try {
            cameraid = manager.getCameraIdList();
            for(String cameraID : cameraid){
                Log.d(TAG, "camera id - "+cameraID);
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraID);
                int facing = characteristics.get(characteristics.LENS_FACING);
                if(facing==CameraCharacteristics.LENS_FACING_FRONT){
                    FRONT_FACING = cameraID;

                    imageDimensionFront = getOptimizedSize(characteristics);

                }else if(facing==CameraCharacteristics.LENS_FACING_BACK){
                    REAR_FACING = cameraID;

                    imageDimensionBack = getOptimizedSize(characteristics);
                }else{
                    Log.d(TAG, "Neither Front or Rear camera found");
                    return;
                }
            }
            if(currentCamera==null){
                currentCamera = REAR_FACING;
            }
        } catch (CameraAccessException e) {
            Log.d(TAG, "camera access exception while trying to get Camera ID list", e);
        }

    }

    public void setDimensionsAndOldCamera(int id){
        oldCamera = Camera.open(id);
        Log.d(TAG, "camera opened id "+id);
        Camera.Parameters parameters = oldCamera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        if(oldCameraIdFlag==1){
            oldCameraFrontSize = new Size(size.width, size.height);
        }else if(oldCameraIdFlag==0){
            oldCameraRearSize = new Size(size.width, size.height);
        }
    }
}
