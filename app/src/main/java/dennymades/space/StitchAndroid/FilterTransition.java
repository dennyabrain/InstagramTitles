package dennymades.space.StitchAndroid;

import android.accessibilityservice.AccessibilityService;
import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.Animation;

/**
 * Created by abrain on 11/1/16.
 */
public class FilterTransition implements ValueAnimator.AnimatorUpdateListener, ValueAnimator.AnimatorListener{
    private ValueAnimator animator;
    private MainActivity mActivity;
    private final String TAG = FilterTransition.class.getSimpleName();

    public FilterTransition(MainActivity activity, float height){
        mActivity = activity;
        animator = ValueAnimator.ofFloat(0.0f, 100.0f);
        animator.setDuration(500);
        animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        animator.addUpdateListener(this);
        animator.addListener(this);
    }

    public void start(){
        animator.start();
        //Log.d(TAG, "animation started");
        mActivity.setTransitionState(1);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        //update value of radius in myGlSurfaceView
        //Log.d(TAG, "animation updating");
        mActivity.updateRadius((float)valueAnimator.getAnimatedFraction());
    }

    @Override
    public void onAnimationStart(Animator animator) {
        mActivity.setTransitionState(1);
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        //Log.d(TAG, "animation ended");
        mActivity.updateRadius(1.0f);
        mActivity.setTransitionState(0);
    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
