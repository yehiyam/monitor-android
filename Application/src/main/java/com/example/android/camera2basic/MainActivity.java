package com.example.android.camera2basic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startTakingPicturesButton = findViewById(R.id.start_taking_pictures_button);
        startTakingPicturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });
    }

    private void startCameraActivity() {
        Intent cameraActivityIntent = new Intent(this, CameraActivity.class);
        startActivity(cameraActivityIntent);
    }
}
