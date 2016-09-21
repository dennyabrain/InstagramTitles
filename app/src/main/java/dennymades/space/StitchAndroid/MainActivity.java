package dennymades.space.StitchAndroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import EmojiTextView.MyEmojiTextView;
import EmojiTextView.TextControlFragment;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import util.Permission;

public class MainActivity extends AppCompatActivity implements TextControlFragment.onTextFragmentButtonClickedListener {
    private static String TAG = "Main Activity : ";
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    private MyGLSurfaceView mRenderer;
    private EmojiconEditText mEmojiconEditText;
    //private EmojiconTextView mEmojiconTextView;
    private MyEmojiTextView myEmojiTextView;
    private Button btnText;

    public static Bitmap mEmojiTextBitmap;

    //FAB Logic
    boolean isFabOpen=false;
    private RecordingProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // seek permission for camera, external storage and audio recording
        boolean permissionGranted = Permission.checkPermission(this, permissions);
        if(permissionGranted){

        }else{
            Permission.seekPermission(this, permissions, Permission.PERMISSION_ALL);
        }

        setContentView(R.layout.activity_main);
        mRenderer = (MyGLSurfaceView)findViewById(R.id.renderer_view);
        mEmojiconEditText=(EmojiconEditText)findViewById(R.id.editEmojicon);
        //mEmojiconTextView=(EmojiconTextView) findViewById(R.id.emojiconTextView);
        myEmojiTextView = new MyEmojiTextView(this);
        myEmojiTextView.setResourceById((EmojiconTextView) findViewById(R.id.emojiconTextView));
        myEmojiTextView.setTouchEvents();
        btnText = (Button) findViewById(R.id.btnText);
        myEmojiTextView.loadTypefaces();
        myEmojiTextView.getTextView().setText("Testing \n In \n New York");
        myEmojiTextView.getTextView().setVisibility(View.VISIBLE);

        mProgressBar = new RecordingProgressBar(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onStop();
        //mRenderer.stopRecording();
        //mRenderer.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRenderer.onResume();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case Permission.PERMISSION_ALL:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "camera permission granted");
                }
                if(grantResults.length>0 && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "storage write permission granted");
                }
                if(grantResults.length>0 && grantResults[2]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "audio permission granted");
                }
                break;
        }
    }

    public void btnStop(View v){
        mRenderer.stopRecording();
    }

    public void btnRecord(View v){
        mRenderer.startRecording();
    }

    public void btnText(View v){
        String label =btnText.getText().toString();
        if(label.equals("TEXT")){
            myEmojiTextView.getTextView().setVisibility(View.INVISIBLE);
            mEmojiconEditText.setVisibility(View.VISIBLE);
            mEmojiconEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEmojiconEditText, InputMethodManager.SHOW_IMPLICIT);
            btnText.setText("DONE");
        }
        else if(label.equals("DONE")){
            /*mEmojiconEditText.setCursorVisible(false);
            mEmojiconEditText.buildDrawingCache();
            mEmojiTextBitmap = Bitmap.createBitmap(mEmojiconEditText.getDrawingCache());
            mRenderer.setBitmapShow(true);
            btnText.setText("TEXT");*/
            mEmojiconEditText.setVisibility(View.INVISIBLE);
            myEmojiTextView.getTextView().setVisibility(View.VISIBLE);
            myEmojiTextView.getTextView().setText(mEmojiconEditText.getText().toString());
            btnText.setText("TEXT");
        }
    }

    @Override
    public void onTextPropertyClicked(String label) {
        myEmojiTextView.setColor(label);
    }

    //FAB CLICK
    public void fabRecordClick(View v){
        Log.d("Denny", "fab clicked");
        if(mProgressBar.getVisibilityStatus()){
            mProgressBar.hideProgressBar();
            Log.d("Denny", "hide pbar");
        }else{
            mProgressBar.showProgressBar();
            Log.d("Denny", "show pbar");
        }
    }
}