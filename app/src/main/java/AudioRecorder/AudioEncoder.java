package AudioRecorder;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by abrain on 10/24/16.
 */
public class AudioEncoder {
    private static final String TAG = AudioEncoder.class.getSimpleName();

    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 64000;
    private static final int TIMEOUT_US = 10000;
    private MediaCodec audioEncoder;
    private MediaFormat audioFormat;

    private boolean isEncoding;

    private MediaCodec.BufferInfo bufferInfo;

    private MediaMuxerWrapper muxer;

    private long prevOutputPTSUs = 0;

    public AudioEncoder(MediaMuxerWrapper mux){
        muxer = mux;
        bufferInfo = new MediaCodec.BufferInfo();

        try {
            audioEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        } catch (IOException e) {
            Log.e(TAG, "exception creating encoder of "+MIME_TYPE+" type", e);
        }

        audioFormat = MediaFormat.createAudioFormat(MIME_TYPE,
                SAMPLE_RATE,
                1);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
        //optional stuff
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE,BIT_RATE);

        audioEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    public void start(){
        Log.d(TAG, "encoder started");
        audioEncoder.start();
        isEncoding = true;
    }

    public void stop(){
        Log.d(TAG, "encoder stopped");
        audioEncoder.stop();
        muxer.stopMuxing();
        isEncoding = false;
    }

    public void encode(ByteBuffer rawBuffer, int length, long presentationTimeUs){
        Log.d(TAG, "presentation time"+presentationTimeUs);
        //get input buffer
        if(isEncoding){
            final ByteBuffer[] inputBuffers = audioEncoder.getInputBuffers();

            //dequeue input buffer
            final int inputBufferIndex = audioEncoder.dequeueInputBuffer(TIMEOUT_US);
            Log.d(TAG, "inputBufferIndex "+inputBufferIndex);
            if(inputBufferIndex>=0){
                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();

                if(rawBuffer!=null){
                    //copy ByteBuffer to input buffer
                    inputBuffer.put(rawBuffer);
                }
                if(length<=0){
                    ////enqueue bytebuffer with EOS
                    Log.d(TAG, "encoding media of length : "+  length);
                    audioEncoder.queueInputBuffer(inputBufferIndex, 0, 0, presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                }else{
                    ////enqueue bytebuffer
                    Log.d(TAG, "encoding media of length : "+  length);
                    audioEncoder.queueInputBuffer(inputBufferIndex, 0, length, presentationTimeUs, 0);
                }
            }else{
                Log.d(TAG, "input buffer index less than zero");
            }
        }

        sendToMediaMuxer();
        //get outputByteBuffer
        //take data from outputByteBuffer
        //send to mediamuxer
    }

    public void sendToMediaMuxer(){
        if(audioEncoder==null) return;

        final ByteBuffer[] outputBuffers = audioEncoder.getOutputBuffers();

        final int outputBufferIndex = audioEncoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_US);
        if(outputBufferIndex==MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
            Log.d(TAG, "output format changed");
            muxer.addAudioEncoder(this);
            muxer.startMuxing();

            Log.d(TAG, "muxer started");
        }
        if(outputBufferIndex>=0){
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                // You shoud set output format to muxer here when you target Android4.3 or less
                // but MediaCodec#getOutputFormat can not call here(because INFO_OUTPUT_FORMAT_CHANGED don't come yet)
                // therefor we should expand and prepare output format from buffer data.
                // This sample is for API>=18(>=Android 4.3), just ignore this flag here
                Log.d(TAG, "in BUFFER_FLAG_CODEC_CONFIG");
                bufferInfo.size = 0;
            }
            final ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            muxer.muxAudio(outputBuffer, bufferInfo);
            Log.d(TAG, "outputBufferIndex"+ outputBufferIndex);
            audioEncoder.releaseOutputBuffer(outputBufferIndex, false);
        }else{
            Log.d(TAG, "output buffer index less than zero");
        }

    }

    public MediaCodec getEncoder(){
        return audioEncoder;
    }

    protected long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        prevOutputPTSUs = result;
        return result;
    }

    // async style mediacodec >21 and polling based for <21
}
