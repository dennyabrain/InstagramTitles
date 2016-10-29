package util;

import android.os.Build;

/**
 * Created by abrain on 10/24/16.
 */
public class Compatibility {
    public static boolean isCompatible(int version){
        if(Build.VERSION.SDK_INT<=version)
            return true;
        else
            return false;
    }
}
