package Mediator;

/**
 * Created by abrain on 9/21/16.
 */
public interface StitchMediatorInterface {
    public void onFabRecordClicked();

    public void startTimer();

    public void cancelTimer();

    public void startEncoding();

    public void stopEncoding();

    public boolean getProgressBarVisibility();

    public void showProgressBar();

    public void hideProgressBar();

    public void setProgress(long duration);
}
