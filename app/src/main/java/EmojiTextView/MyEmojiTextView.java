package EmojiTextView;

import android.app.Activity;
import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Typeface;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by abrain on 9/15/16.
 */
public class MyEmojiTextView{
    private String TAG="MyEmojiTextView";
    private EmojiconTextView mEmojiconTextView;
    private Activity mActivity;
    private int currentTypfaceIndex=0;
    private Typeface[] typfaces;

    private GestureDetector mDetector;

    public MyEmojiTextView(Activity activity){
        mActivity=activity;
        mDetector = new GestureDetector(activity, new myGestureDetector());
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
                /*if(!result){
                    Log.d(TAG, "gesture detector returned false");
                }*/
                /*if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    return true;
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    Log.d(TAG, "DOWN");
                    currentTypfaceIndex=(currentTypfaceIndex+1)%6;
                    //mEmojiconTextView.setTypeface(typefaces[currentTypfaceIndex]);
                    setType(currentTypfaceIndex);
                }*/
                return true;
            }
        });
    }



    public void loadTypefaces(){
        //Load all fonts
        typfaces = new Typeface[6];
        typfaces[0]=Typeface.DEFAULT;
        for(int i=1;i<6;i++){
            typfaces[i]=Typeface.createFromAsset(mActivity.getAssets(),"fonts/"+i+".ttf");
        }
    }

    public void setType(int index){
        mEmojiconTextView.setTypeface(typfaces[index]);
    }


    public class myGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "on Single Tap Up");
            currentTypfaceIndex=(currentTypfaceIndex+1)%6;
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
            Log.d(TAG, "on Fling" + velocityX);
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
}
