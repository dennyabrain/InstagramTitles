package dennymades.space.StitchAndroid;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.util.Log;

/**
 * Created by abrain on 11/1/16.
 */
public class FilterTransition implements ValueAnimator.AnimatorUpdateListener{
    private ValueAnimator animator;
    private MainActivity mActivity;
    private final String TAG = FilterTransition.class.getSimpleName();

    public FilterTransition(MainActivity activity, float height){
        mActivity = activity;
        animator = ValueAnimator.ofFloat(0.0f, 500.0f);
        animator.setDuration(250);
        animator.setInterpolator(new android.view.animation.BounceInterpolator());
        animator.addUpdateListener(this);
    }

    public void start(){
        animator.start();
        Log.d(TAG, "animation started");
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        //update value of radius in myGlSurfaceView
        Log.d(TAG, "animation updating");
        mActivity.updateRadius((float)valueAnimator.getAnimatedValue());
    }
}
