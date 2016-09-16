package EmojiTextView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by abrain on 9/15/16.
 */
public class MyEmojiTextView {
    private String TAG="MyEmojiTextView";
    private EmojiconTextView mEmojiconTextView;
    private Activity mActivity;
    private int currentTypfaceIndex=0;
    private Typeface[] typfaces;

    public MyEmojiTextView(Activity activity){
        mActivity=activity;
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
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    Log.d(TAG, "DOWN");
                    currentTypfaceIndex=(currentTypfaceIndex+1)%6;
                    //mEmojiconTextView.setTypeface(typefaces[currentTypfaceIndex]);
                    setType(currentTypfaceIndex);
                }
                return false;
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
}
