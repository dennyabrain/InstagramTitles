package Mediator;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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

    public StitchMediator(Context context, Activity activity){
        mContext = context;
        mActivity = activity;
        mRecordingProgressBar = new RecordingProgressBar(mActivity, this);
        mVideoCountDown = new VideoCountDown(this);
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
    }

    @Override
    public void cancelTimer() {
        mVideoCountDown.cancel();
    }

    @Override
    public void startEncoding() {

    }

    @Override
    public void stopEncoding() {

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
