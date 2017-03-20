package mobapptut.com.camera2videoimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    private void onClick(View v){
        Intent intent = new Intent(this, Camera2VideoImageActivity.class);
        startActivity(intent);
    }
}
