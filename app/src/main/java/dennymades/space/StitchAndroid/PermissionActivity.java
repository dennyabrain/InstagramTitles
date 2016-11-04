package dennymades.space.StitchAndroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import util.FullScreen;
import util.Permission;

public class PermissionActivity extends AppCompatActivity {
    private final String TAG = PermissionActivity.class.getSimpleName();
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        // seek permission for camera, external storage and audio recording
        boolean permissionGranted = Permission.checkPermission(this, permissions);
        if(permissionGranted){
            /*Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slidefromright, R.anim.slidetoleft);*/
        }else{
            Permission.seekPermission(this, permissions, Permission.PERMISSION_ALL);
        }
        FullScreen.activateImmersiveMode(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case Permission.PERMISSION_ALL:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "camera permission granted");
                }
                if(grantResults.length>0 && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "storage write permission granted");
                }
                if(grantResults.length>0 && grantResults[2]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "audio permission granted");
                }
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        finish();
        //Intent intent = new Intent(this, OnboardActivity.class);
        //startActivity(intent);
        overridePendingTransition(R.anim.slidefromleft, R.anim.slidetoright);
    }
}

