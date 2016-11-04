package dennymades.space.StitchAndroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import util.FullScreen;

public class OnboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        FullScreen.activateImmersiveMode(this);
    }

    public void btnOK(View v){
        Intent intent = new Intent(this, PermissionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slidefromright, R.anim.slidetoleft);
    }
}
