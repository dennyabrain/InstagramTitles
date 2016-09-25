package Mediator;

import android.app.Activity;
import android.content.Context;
import android.provider.DocumentsContract;
import android.util.Log;

import GUI.RootLayout;
import dennymades.space.StitchAndroid.MyGLSurfaceView;
import dennymades.space.StitchAndroid.R;
import dennymades.space.StitchAndroid.RecordingProgressBar;
import dennymades.space.StitchAndroid.VideoCountDown;

/**
 * Created by abrain on 9/21/16.
 */
public class StitchMediator implements StitchMediatorInterface {
    private Context mContext;
    private Activity mActivity;
    private RecordingProgressBar mRecordingProgressBar;
    private VideoCountDown mVideoCountDown;
    private MyGLSurfaceView mRenderer;
    private RootLayout rootLayout;

    public StitchMediator(Context context, Activity activity){
        mContext = context;
        mActivity = activity;
        mRecordingProgressBar = new RecordingProgressBar(mActivity, this);
        mVideoCountDown = new VideoCountDown(this);

        mRenderer = (MyGLSurfaceView)activity.findViewById(R.id.renderer_view);

        rootLayout = new RootLayout(R.id.rootLayout, this);
    }

    @Override
    public Activity getActivity() {
        return mActivity;
    }

    @Override
    public void onFabRecordClicked() {
        if(getProgressBarVisibility()){
            hideProgressBar();
        }else{
            showProgressBar();
        }
    }

    @Override
    public void startTimer() {
        mVideoCountDown.start();
        mRenderer.startRecording();
    }

    @Override
    public void cancelTimer() {
        mVideoCountDown.cancel();
        mRenderer.stopRecording();
    }

    @Override
    public void startEncoding() {
        mRenderer.startRecording();
    }

    @Override
    public void stopEncoding() {
        mRenderer.stopRecording();
    }

    @Override
    public void resumeEncoding() {
        mRenderer.onResume();
    }

    @Override
    public void setBitmapShow(boolean b) {
        mRenderer.setBitmapShow(b);
    }

    @Override
    public boolean getProgressBarVisibility() {
        return mRecordingProgressBar.getVisibilityStatus();
    }

    @Override
    public void showProgressBar() {
        mRecordingProgressBar.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        mRecordingProgressBar.hideProgressBar();
    }

    @Override
    public void setProgress(long duration) {
        mRecordingProgressBar.setProgress(100-(int)(duration/100));
    }
}
