package dennymades.space.StitchAndroid;

import android.os.CountDownTimer;
import android.util.Log;

import Mediator.StitchMediator;

/**
 * Created by abrain on 9/21/16.
 */
public class VideoCountDown {
    private StitchMediator mMediator;
    private CountDownTimer mCountDownTimer;

    public VideoCountDown(StitchMediator m){
        mMediator = m;
        mCountDownTimer = new CountDownTimer(10000,100) {
            @Override
            public void onTick(long l) {
                //mProgressBar.setProgress(100-(int)(l/100));
                mMediator.setProgress(l);
            }

            @Override
            public void onFinish() {
                mMediator.setProgress(100);
                mMediator.hideProgressBar();
                Log.d("Denny", "countdown Over");
            }
        };
    }

    public void start(){
        mCountDownTimer.start();
    }

    public void cancel(){
        mCountDownTimer.cancel();
    }
}
