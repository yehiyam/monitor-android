/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.camera2basic;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class CameraActivity extends AppCompatActivity {

    public final String TAG = "CameraActivity";

    public final String IMAGE_FREQUENCY_KEY = "IMAGE_FREQUENCY";
    public final int IMAGE_FREQUENCY_DEFAULT_MILI = 5000;

    public final int DELAY_BEFORE_TAKING_PICTURES_MILLIS = 2000;

    public int imageFrequencyMili;

    String IMAGE_ID_KEY = "image_ID";
    int imageId;

    Handler handler;

    Runnable takingPicturesRunnable;

    private Camera2BasicFragment camera2BasicFragment;

    Button stopTakingPicturesButton;
    private SharedPreferences preference;

    //todo: remove after debug
    private long lastRunTime = 0;

    private Size imageResolution;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // load image frequency from shared preference
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        imageFrequencyMili = preference.getInt(IMAGE_FREQUENCY_KEY, IMAGE_FREQUENCY_DEFAULT_MILI);

        imageResolution = new Size(1920, 1080);

        imageId = preference.getInt(IMAGE_ID_KEY, 1);

        camera2BasicFragment = Camera2BasicFragment.newInstance();
        stopTakingPicturesButton = findViewById(R.id.stop_taking_pictures_button);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.camera_container, camera2BasicFragment)
                    .commit();
        }

        handler = new Handler();
        takingPicturesRunnable = new Runnable() {
            @Override
            public void run() {
                camera2BasicFragment.takePicture();

                //todo: remove after debug
                Log.d(TAG, "imageFrequency:" + imageFrequencyMili);
                Log.d(TAG, String.format("dt: %d", System.currentTimeMillis() - lastRunTime));
                lastRunTime = System.currentTimeMillis();

                handler.postDelayed(takingPicturesRunnable, imageFrequencyMili);
            }
        };

        stopTakingPicturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTakingPicturesDialog();
            }
        });

        startTakingPictures();
    }

    public void addToImageIdCounter() {
        imageId += 1;
        preference.edit().putInt(IMAGE_ID_KEY, imageId).apply();
    }

    public void updateImageFrequency(int frequency) {
        imageFrequencyMili = frequency;
        preference.edit().putInt(IMAGE_FREQUENCY_KEY, imageFrequencyMili).apply();
    }

    public void startTakingPictures() {
        if (!handler.hasCallbacks(takingPicturesRunnable)) {
            Log.i(TAG, "startTakingPictures");
            handler.postDelayed(takingPicturesRunnable, DELAY_BEFORE_TAKING_PICTURES_MILLIS);
        }
    }

    public void stopTakingPictures() {
        handler.removeCallbacks(takingPicturesRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTakingPictures();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTakingPictures();
    }

    public void stopTakingPicturesDialog() {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage(R.string.stop_taking_pictures_dialog)
                .setPositiveButton(R.string.stop_taking_picture_dialog_keep_taking_pictures, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(R.string.stop_taking_pictures_dialog_stop_taking_pictures, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopTakingPictures();
                        finish();
                    }
                }).show();
    }

    public int getImageId() {
        return imageId;
    }

    public Size getImageResolution() {
        return imageResolution;
    }

    public void setImageResolution(Size imageResolution) {
        this.imageResolution = imageResolution;
    }
}