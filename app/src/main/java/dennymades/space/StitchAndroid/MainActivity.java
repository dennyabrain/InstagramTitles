package dennymades.space.StitchAndroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import util.Permission;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "Main Activity : ";
    private String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    private MyGLSurfaceView mRenderer;

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
        mRenderer.stopRecording();
        mRenderer.onDestroy();
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
}
