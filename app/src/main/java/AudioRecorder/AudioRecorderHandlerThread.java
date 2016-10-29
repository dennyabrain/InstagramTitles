package AudioRecorder;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import util.Messages;

/**
 * Created by abrain on 10/24/16.
 */
public class AudioRecorderHandlerThread extends HandlerThread implements Handler.Callback{
    private static final String TAG = AudioRecorderHandlerThread.class.getSimpleName();

    /* Handler associated with this HandlerThread*/
    private Handler mHandler;

    /* Reference to a handler from the thread that started this HandlerThread */
    private Handler mCallback;

    private static final int MSG_RECORDING_START = 100;
    private static final int MSG_RECORDING_STOP = 101;

    /* AudioRecord object to record audio from microphone input */
    private AudioRecorder audioRecorder;

    /* AudioEncoder object to take recorded ByteBuffer from the AudioRecord object*/
    private AudioEncoder audioEncoder;

    /* MediaMuxerWrapper object to add encoded data to a MediaMuxer which converts it to .mp4*/
    private MediaMuxerWrapper mediaMuxerWrapper;

    public AudioRecorderHandlerThread(String name) {
        super(name);
        mediaMuxerWrapper = new MediaMuxerWrapper();
        audioEncoder = new AudioEncoder(mediaMuxerWrapper);
        audioRecorder = new AudioRecorder(audioEncoder);
    }

    public AudioRecorderHandlerThread(String name, int priority) {
        super(name, priority);
        mediaMuxerWrapper = new MediaMuxerWrapper();
        audioEncoder = new AudioEncoder(mediaMuxerWrapper);
        audioRecorder = new AudioRecorder(audioEncoder);
    }

    public void setCallback(Handler cb){
        mCallback = cb;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler(getLooper(), this);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch(message.what){
            case MSG_RECORDING_START:
                Log.d(TAG,  "recording start message received");
                //mCallback.sendMessage(Message.obtain(null, Messages.MSG_AUDIO_RECORDING_START_CALLBACK));
                audioRecorder.start();
                audioEncoder.start();
                audioRecorder.record();
                break;
            case MSG_RECORDING_STOP:
                Log.d(TAG,  "recording stop message received");
                //mCallback.sendMessage(Message.obtain(null, Messages.MSG_AUDIO_RECORDING_STOP_CALLBACK));
                audioRecorder.stopRecording();
                break;
        }
        return true;
    }

    public void startRecording(){
        Log.d(TAG, "stop Recording");
        Message msg = Message.obtain(null, MSG_RECORDING_START);
        mHandler.sendMessage(msg);
    }

    public void stopRecording(){
        Log.d(TAG, "stop Recording");
        audioRecorder.setIsRecordingFalse();
        Message msg = Message.obtain(null, MSG_RECORDING_STOP);
        mHandler.sendMessage(msg);
    }
}
