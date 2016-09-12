package util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by abrain on 9/2/16.
 */
public class FileManager {
    public static final int MEDIA_TYPE_IMAGE=1;
    public static final int MEDIA_TYPE_VIDEO=2;
    private static String TAG = "FileManager : ";

    public static File getOutputMediaFile(int mediaType){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Stitch");

        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                Log.d(TAG, "error creating directory");
                return null;
            }
        }

        //generate unique filename
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        if(mediaType==MEDIA_TYPE_IMAGE){
            //do stuff for saving pictures. todo later
            return null;
        }else if(mediaType==MEDIA_TYPE_VIDEO){
            mediaFile = new File(mediaStorageDir.getPath()+File.separator+"MP4_"+timestamp+".mp4");
        }else{
            return null;
        }

        return mediaFile;
    }
}
