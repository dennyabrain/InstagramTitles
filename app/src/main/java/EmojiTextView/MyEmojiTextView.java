package EmojiTextView;

import android.app.Activity;
import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Color;
import android.graphics.Typeface;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import dennymades.space.StitchAndroid.MainActivity;
import dennymades.space.StitchAndroid.R;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by abrain on 9/15/16.
 */
public class MyEmojiTextView implements View.OnDragListener{
    private String TAG="MyEmojiTextView";
    private EmojiconTextView mEmojiconTextView;
    private Activity mActivity;
    private int currentTypfaceIndex=0;
    private Typeface[] typfaces;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TextControlFragment textControlFragment;
    private GestureDetector mDetector;
    private boolean overlayUIVisible;

    public MyEmojiTextView(Activity activity){
        mActivity=activity;
        mDetector = new GestureDetector(activity, new myGestureDetector());
        fragmentManager =activity.getFragmentManager();
        overlayUIVisible = false;
    }

    public void setResourceById(EmojiconTextView id){
        mEmojiconTextView = id;
    }

    public EmojiconTextView getTextView(){
        return mEmojiconTextView;
    }

    public void setTouchEvents(){
        mEmojiconTextView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean result = mDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    public void setDragEvents(){
        mEmojiconTextView.setOnDragListener(this);
    }

    public void setColor(String s){
        if(s.equals("V")){
            mEmojiconTextView.setTextColor(Color.parseColor("#8C2155"));
        }
        if(s.equals("I")){
            mEmojiconTextView.setTextColor(Color.parseColor("#0B3948"));
        }
        if(s.equals("B")){
            mEmojiconTextView.setTextColor(Color.parseColor("#63ADF2"));
        }
        if(s.equals("G")){
            mEmojiconTextView.setTextColor(Color.parseColor("#A2FAA3"));
        }
        if(s.equals("Y")){
            mEmojiconTextView.setTextColor(Color.parseColor("#FEFFA5"));
        }
        if(s.equals("O")){
            mEmojiconTextView.setTextColor(Color.parseColor("#D14127"));
        }
        if(s.equals("R")){
            mEmojiconTextView.setTextColor(Color.parseColor("#FF2B2B"));
        }
    }



    public void loadTypefaces(){
        //Load all fonts
        typfaces = new Typeface[7];
        typfaces[0]=Typeface.DEFAULT;
        for(int i=1;i<7;i++){
            typfaces[i]=Typeface.createFromAsset(mActivity.getAssets(),"fonts/"+i+".ttf");
        }
    }

    public void setType(int index){
        mEmojiconTextView.setTypeface(typfaces[index]);
    }

    public void toggleOverlayUI(){
        if(overlayUIVisible==false){
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.fadein,R.animator.fadeout);
            textControlFragment = new TextControlFragment();
            fragmentTransaction.add(R.id.fragmentViewGroup, textControlFragment);
            fragmentTransaction.commit();
            overlayUIVisible=true;
        }else{
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.fadein,R.animator.fadeout);
            fragmentTransaction.hide(textControlFragment);
            //fragmentTransaction.remove(textControlFragment);
            fragmentTransaction.commit();
            overlayUIVisible=false;
        }

    }


    public class myGestureDetector extends GestureDetector.SimpleOnGestureListener{


        @Override
        public void onLongPress(MotionEvent e) {
            toggleOverlayUI();
            Log.d(TAG, "Long Press");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "on Single Tap Up");
            currentTypfaceIndex=(currentTypfaceIndex+1)%7;
            setType(currentTypfaceIndex);
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            //return super.onDown(e);
            Log.d(TAG, "on Down");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if(velocityX<0){
                mEmojiconTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            }else{
                mEmojiconTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }
            mEmojiconTextView.setText(mEmojiconTextView.getText());
            //return super.onFling(e1, e2, velocityX, velocityY);
            return true;
        }
    }

    //DRAG AND DROP LISTENERS

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        Log.d(TAG, "in drag");
        return true;
    }
}
