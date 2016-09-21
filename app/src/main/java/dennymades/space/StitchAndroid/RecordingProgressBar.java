package dennymades.space.StitchAndroid;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;

/**
 * Created by abrain on 9/21/16.
 */
public class RecordingProgressBar {
    private String TAG = this.getClass().getName();
    private ProgressBar mProgressBar;
    private ObjectAnimator animAlphaUp;
    private ObjectAnimator animTranslateYUp;
    private ObjectAnimator animAlphaDn;
    private ObjectAnimator animTranslateYDn;
    private boolean isProgressBarVisible=false;
    private CountDownTimer mCountDownTimer;

    public RecordingProgressBar(Activity activity){
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

        mCountDownTimer = new CountDownTimer(10000,100) {
            @Override
            public void onTick(long l) {
                mProgressBar.setProgress(100-(int)(l/100));
            }

            @Override
            public void onFinish() {
                mProgressBar.setProgress(100);
                animTranslateYDn.start();
                animAlphaDn.start();
                isProgressBarVisible=false;
                Log.d("Denny", "countdown Over");

            }
        };
    }

    public void hideProgressBar(){
        animTranslateYDn.start();
        animAlphaDn.start();
        isProgressBarVisible=false;
        mCountDownTimer.cancel();
    }

    public void showProgressBar(){
        animAlphaUp.start();
        animTranslateYUp.start();
        isProgressBarVisible=true;
        mCountDownTimer.start();
    }

    public boolean getVisibilityStatus(){
        if(isProgressBarVisible){
            return true;
        }else{
            return false;
        }
    }


}
