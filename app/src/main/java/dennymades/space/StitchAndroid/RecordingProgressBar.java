package dennymades.space.StitchAndroid;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;

import Mediator.StitchMediator;

/**
 * Created by abrain on 9/21/16.
 */
public class RecordingProgressBar {
    private StitchMediator mMediator;
    private String TAG = this.getClass().getName();
    private ProgressBar mProgressBar;
    private ObjectAnimator animAlphaUp;
    private ObjectAnimator animTranslateYUp;
    private ObjectAnimator animAlphaDn;
    private ObjectAnimator animTranslateYDn;
    private boolean isProgressBarVisible=false;
    private CountDownTimer mCountDownTimer;

    public RecordingProgressBar(Activity activity, StitchMediator m){
        mMediator = m;
        mProgressBar = (ProgressBar) activity.findViewById(R.id.progressBar);

        animAlphaUp = ObjectAnimator.ofFloat(mProgressBar, "alpha", 0.5f, 1f);
        animAlphaUp.setDuration(300);
        animTranslateYUp = ObjectAnimator.ofFloat(mProgressBar, "translationY", 0,-150);
        animTranslateYUp.setDuration(300);
        animTranslateYUp.setInterpolator(new OvershootInterpolator());

        animAlphaDn = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1f, 0.5f);
        animAlphaDn.setDuration(300);
        animTranslateYDn = ObjectAnimator.ofFloat(mProgressBar, "translationY", -150,0);
        animTranslateYDn.setDuration(300);
        animTranslateYDn.setInterpolator(new OvershootInterpolator());
    }

    public void hideProgressBar(){
        animTranslateYDn.start();
        animAlphaDn.start();
        isProgressBarVisible=false;
        mMediator.cancelTimer();
    }

    public void showProgressBar(){
        animAlphaUp.start();
        animTranslateYUp.start();
        isProgressBarVisible=true;
        mMediator.startTimer();
    }

    public boolean getVisibilityStatus(){
        if(isProgressBarVisible){
            return true;
        }else{
            return false;
        }
    }

    public void setProgress(int a){
        mProgressBar.setProgress(a);
    }
}
