package dennymades.space.StitchAndroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaMuxer;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.io.IOException;

import AudioRecorder.AudioRecorderHandlerThread;
import EmojiTextView.MyEmojiTextView;
import EmojiTextView.TextControlFragment;
import Encoder.TextureMovieEncoder;
import Encoder.VideoEncoderCore;
import Mediator.StitchMediator;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import util.Compatibility;
import util.FileManager;
import util.Messages;
import util.Permission;

public class MainActivity extends AppCompatActivity implements TextControlFragment.onTextFragmentButtonClickedListener, Handler.Callback {
    private static String TAG = "Main Activity : ";
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    //private MyGLSurfaceView mRenderer;
    private EmojiconEditText mEmojiconEditText;
    //private EmojiconTextView mEmojiconTextView;
    private MyEmojiTextView myEmojiTextView;
    private Button btnText;

    public static Bitmap mEmojiTextBitmap;
    FloatingActionButton fab;

    private AudioRecorderHandlerThread audioRecorderHandlerThread;
    private MyGLSurfaceView myGLSurfaceView;

    //FAB Logic
    //boolean isFabOpen=false;
    //private RecordingProgressBar mProgressBar;

    //Mediator
    private StitchMediator stitchMediator;

    private MediaMuxer mMuxer;

    private Handler mainActivityHandler;

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

        mainActivityHandler = new Handler(this);

        myGLSurfaceView = (MyGLSurfaceView) findViewById(R.id.renderer_view);

        try {
                mMuxer = new MediaMuxer(FileManager.getOutputMediaFile(2).toString(),
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            Log.d(TAG, "IO exception creating new muxer - ",e);
        }
        //mRenderer = (MyGLSurfaceView)findViewById(R.id.renderer_view);
        mEmojiconEditText=(EmojiconEditText)findViewById(R.id.editEmojicon);
        //mEmojiconTextView=(EmojiconTextView) findViewById(R.id.emojiconTextView);
        myEmojiTextView = new MyEmojiTextView(this);
        myEmojiTextView.setResourceById((EmojiconTextView) findViewById(R.id.emojiconTextView));
        myEmojiTextView.setTouchEvents();
        myEmojiTextView.setDragEvents();
        btnText = (Button) findViewById(R.id.btnText);
        myEmojiTextView.loadTypefaces();
        fab = (FloatingActionButton)findViewById(R.id.fabRecord);
        //myEmojiTextView.getTextView().setText("Testing \n In \n New York");
        //myEmojiTextView.getTextView().setVisibility(View.VISIBLE);

        //mProgressBar = new RecordingProgressBar(this);
        stitchMediator = new StitchMediator(this, this);
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
        if(Compatibility.isCompatible(16))
            audioRecorderHandlerThread.quit();
        else
            audioRecorderHandlerThread.quitSafely();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        audioRecorderHandlerThread = new AudioRecorderHandlerThread("Audio Recorder Thread", Process.THREAD_PRIORITY_URGENT_AUDIO);
        audioRecorderHandlerThread.setCallback(mainActivityHandler);
        audioRecorderHandlerThread.start();
        myGLSurfaceView.setAudioRecorderHandler(audioRecorderHandlerThread);
        myGLSurfaceView.setCallback(mainActivityHandler);
        stitchMediator.resumeEncoding();
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

    public void btnText(View v){
        String label=btnText.getText().toString();

        if(label.equals("TEXT")){
            //myEmojiTextView.getTextView().setVisibility(View.INVISIBLE);
            mEmojiconEditText.setVisibility(View.VISIBLE);
            mEmojiconEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEmojiconEditText, InputMethodManager.SHOW_IMPLICIT);
            btnText.setText("DONE");
        }
        else if(label.equals("DONE")){
            mEmojiconEditText.setCursorVisible(false);
            //mEmojiTextBitmap = Bitmap.createBitmap(mEmojiconEditText.getDrawingCache());
            btnText.setText("TEXT");
            mEmojiconEditText.setVisibility(View.INVISIBLE);
            myEmojiTextView.getTextView().setVisibility(View.VISIBLE);
            myEmojiTextView.getTextView().setText(mEmojiconEditText.getText().toString());
            /*mEmojiconEditText.setVisibility(View.INVISIBLE);
            myEmojiTextView.getTextView().setVisibility(View.VISIBLE);
            myEmojiTextView.getTextView().setText(mEmojiconEditText.getText().toString());
            btnText.setText("TEXT");*/
        }
    }

    @Override
    public void onTextPropertyClicked(String label) {
        myEmojiTextView.setColor(label);
    }

    //FAB CLICK
    public void fabRecordClick(View v){
        if(stitchMediator.getProgressBarVisibility()){
            //hideProgressBar();
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_record));
        }else{
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause_black_24dp));
            myEmojiTextView.getTextView().setVisibility(View.INVISIBLE);
            myEmojiTextView.getTextView().buildDrawingCache();
            mEmojiTextBitmap = Bitmap.createBitmap(myEmojiTextView.getTextView().getDrawingCache());
            stitchMediator.setBitmapShow(true);
        }
        stitchMediator.onFabRecordClicked();
    }

    @Override
    public boolean handleMessage(Message message) {
        switch(message.what){
            case Messages.REQUEST_MUXER:
                Log.d(TAG, "REQUEST FOR MUXER RECEIVED");
                TextureMovieEncoder t = myGLSurfaceView.getTextureMovieEncoder();
                t.setMuxer(mMuxer);
                audioRecorderHandlerThread.setMuxer(mMuxer);
                break;
        }
        return true;
    }
}