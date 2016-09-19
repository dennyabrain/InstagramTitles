package EmojiTextView;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.Button;
import android.widget.LinearLayout;

import org.w3c.dom.Text;

import dennymades.space.StitchAndroid.MainActivity;
import dennymades.space.StitchAndroid.R;
import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by abrain on 9/18/16.
 */
public class TextControlFragment extends android.app.Fragment implements View.OnClickListener{
    private String TAG = "TextControlFragment";
    private EmojiconTextView mEmojiconTextView;
    onTextFragmentButtonClickedListener mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.text_control_fragment, container, false);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup) view.findViewById(R.id.fragmentRootView);
        View v;
        for(int i=0;i<vg.getChildCount();i++){
            v = vg.getChildAt(i);
            if(v instanceof LinearLayout){
                View v2;
                for(int j=0;j<((LinearLayout) v).getChildCount();j++){
                    v2 = ((LinearLayout) v).getChildAt(j);
                    if(v2 instanceof Button){
                        v2.setOnClickListener(this);
                    }
                }
            }
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (onTextFragmentButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface onTextFragmentButtonClickedListener {
        public void onTextPropertyClicked(String label);
    }

    @Override
    public void onClick(View view) {
        Button b = (Button)view;
        String label = b.getTag().toString();
        //String label = b.getText().toString();
        mCallback.onTextPropertyClicked(label);
    }
}
