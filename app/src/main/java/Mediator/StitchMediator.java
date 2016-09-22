package Mediator;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import dennymades.space.StitchAndroid.RecordingProgressBar;

/**
 * Created by abrain on 9/21/16.
 */
public class StitchMediator implements StitchMediatorInterface {
    private Context mContext;
    private Activity mActivity;
    private RecordingProgressBar mRecordingProgressBar;

    public StitchMediator(Context context, Activity activity){
        mContext = context;
        mActivity = activity;
        mRecordingProgressBar = new RecordingProgressBar(mActivity);
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
    public void startEncoding() {

    }

    @Override
    public void startTimer() {

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
}
