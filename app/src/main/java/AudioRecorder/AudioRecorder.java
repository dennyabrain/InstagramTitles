package AudioRecorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

import util.Messages;

/**
 * Created by abrain on 10/24/16.
 * a wrapper around android's AudioRecord class
 * meant to record audio from microphone input
 * to be operated from the a HandlerThread (AudioRecorderHandlerThread)
 */
public class AudioRecorder {
    private static final String TAG = AudioRecorder.class.getSimpleName();
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private static final int SAMPLES_PER_FRAME = 1024;
    private static final int FRAMES_PER_BUFFER = 25;

    private int bufferSizeInBytes;

    private AudioRecord mAudioRecord;

    private AudioEncoder audioEncoder;

    private boolean isRecording;

    private boolean isEncoding = false;

    private Handler mCallback;

    public AudioRecorder(AudioEncoder encoder, Handler UICallback){
        mCallback = UICallback;
        audioEncoder = encoder;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT);

        //todo understand this logic
        int bufferSize = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
        if(bufferSize<bufferSizeInBytes){
            bufferSize = ((bufferSizeInBytes/SAMPLES_PER_FRAME)+1)*SAMPLES_PER_FRAME*2;
        }

        mAudioRecord = new AudioRecord(AUDIO_SOURCE,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSizeInBytes);
        isRecording = false;
    }

    public void start(){
        mAudioRecord.startRecording();
        isRecording = true;
    }

    public void startEncoding(){
        isEncoding = true;
    }

    public void record(){
        final ByteBuffer bytebuffer = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
        int bufferReadResult;

        while(isRecording){
            bytebuffer.clear();
            bufferReadResult = mAudioRecord.read(bytebuffer,SAMPLES_PER_FRAME);

            if(bufferReadResult==AudioRecord.ERROR_INVALID_OPERATION || bufferReadResult==AudioRecord.ERROR_BAD_VALUE){
                Log.d(TAG, "audio record read error");
            }else if(bufferReadResult>=0){
                //Log.d(TAG, "bytes read "+bufferReadResult);
                // todo send this byte array to an audio encoder
                //Log.d(TAG, "going to encode "+bufferReadResult);
                bytebuffer.position(bufferReadResult);
                bytebuffer.flip();
                byte[] bytes = new byte[bytebuffer.remaining()];
                bytebuffer.get(bytes);
                String packet = new String(bytes);
                //Log.d(TAG, packet);

                bytebuffer.position(bufferReadResult);
                bytebuffer.flip();

                if(isEncoding){
                    audioEncoder.encode(bytebuffer, bufferReadResult, audioEncoder.getPTSUs());
                }
                //send amplitude to myGLSurfaceView
                byte[] a = bytebuffer.array();
                float maxAmp = 0;
                for(int i=0;i<a.length;i++){
                    if(Math.abs(a[i])>maxAmp){
                        maxAmp = a[i];
                    }
                }
                //float avg = Math.abs(sum/a.length);
                //Log.d(TAG, "maxAmp : "+maxAmp);
                mCallback.sendMessage(Message.obtain(null, Messages.MSG_LOUDNESS, maxAmp));
            }
        }
    }

    public void sendEOS(){
        //Log.d(TAG, "sending EOS");
        final ByteBuffer bytebuffer = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
        int bufferReadResult;

        bufferReadResult = mAudioRecord.read(bytebuffer,SAMPLES_PER_FRAME);
        audioEncoder.encode(bytebuffer, 0, audioEncoder.getPTSUs());
    }

    public void stopRecording(){
        mAudioRecord.stop();
        mAudioRecord.release();
        sendEOS();
        audioEncoder.stop();
        //possibly send an EOS to encoder.
    }

    public void setIsRecordingFalse(){
        isRecording = false;
        isEncoding = false;
    }


}
