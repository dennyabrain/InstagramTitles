package Mediator;

import android.app.Activity;

/**
 * Created by abrain on 9/21/16.
 */
public interface StitchMediatorInterface {
    //BASE STUFF
    public Activity getActivity();

    //GUI STUFF
    public void onFabRecordClicked();

    //TIMER STUFF
    public void startTimer();

    public void cancelTimer();

    //ENCODER STUFF
    public void startEncoding();

    public void stopEncoding();

    public void setBitmapShow(boolean b);

    public void resumeEncoding();

    //PROGRESS BAR RELATED STUFF
    public boolean getProgressBarVisibility();

    public void showProgressBar();

    public void hideProgressBar();

    public void setProgress(long duration);

}
