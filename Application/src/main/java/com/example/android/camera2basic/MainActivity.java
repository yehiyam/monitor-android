package com.example.android.camera2basic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.slf4j.LoggerFactory;

import java.util.Objects;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class MainActivity extends AppCompatActivity {

    public static final String IMAGE_RESOLUTION_INDEX = "IMAGE_RESOLUTION_INDEX";
    public static final String IMAGE_RESOLUTION_STRING = "IMAGE_RESOLUTION_STRING";
    public static final String SERVER_URL_STRING = "SERVER_URL_STRING";
    Button resolutionPlus;
    Button resolutionMinus;

    TextView currentResolution;
    EditText serverUrlEt;

    ResolutionViewModel resolutionViewModel;

    private Size imageResolution;

    public Size[] supportedResolution;
    private SharedPreferences preference;
    private String image_resolution_index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton startTakingPicturesButton = findViewById(R.id.start_taking_pictures_button);
        startTakingPicturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });

        preference = PreferenceManager.getDefaultSharedPreferences(this);

        supportedResolution = getSupportedResolutions();

        resolutionPlus = findViewById(R.id.plus);
        resolutionMinus = findViewById(R.id.minus);
        currentResolution = findViewById(R.id.resolution_string_tv);

        serverUrlEt = findViewById(R.id.server_url_et);
        serverUrlEt.setText(R.string.default_server_url);


        final Observer<Integer> resolutionObserver  = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                imageResolution = supportedResolution[resolutionViewModel.getResolutionIndex().getValue()];
                currentResolution.setText(imageResolution.toString());
            }
        };


        resolutionViewModel = ViewModelProviders.of(this).get(ResolutionViewModel.class);
        resolutionViewModel.getResolutionIndex().setValue(preference.getInt(IMAGE_RESOLUTION_INDEX, 0));

        resolutionViewModel.getResolutionIndex().observe(this, resolutionObserver);

        resolutionPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseResolutionValue();
            }
        });

        resolutionMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseResolutionValue();
            }
        });

        configureLogbackDirectly();
        startCameraActivity();

    }

    public void increaseResolutionValue() {
        if (supportedResolution == null) {
            return;
        }

        resolutionViewModel.getResolutionIndex().setValue(Math.min(resolutionViewModel.getResolutionIndex().getValue() + 1, supportedResolution.length - 1));
        preference.edit().putInt(IMAGE_RESOLUTION_INDEX, resolutionViewModel.getResolutionIndex().getValue()).apply();

    }

    public void decreaseResolutionValue() {
        resolutionViewModel.getResolutionIndex().setValue(Math.max(resolutionViewModel.getResolutionIndex().getValue() - 1, 0));
        preference.edit().putInt(IMAGE_RESOLUTION_INDEX, resolutionViewModel.getResolutionIndex().getValue()).apply();
    }

    private void startCameraActivity() {
        Intent cameraActivityIntent = new Intent(this, CameraActivity.class);
        cameraActivityIntent.putExtra(IMAGE_RESOLUTION_INDEX, resolutionViewModel.getResolutionIndex().getValue());
        cameraActivityIntent.putExtra(IMAGE_RESOLUTION_STRING, supportedResolution[resolutionViewModel.getResolutionIndex().getValue()].toString());
        cameraActivityIntent.putExtra(SERVER_URL_STRING, serverUrlEt.getText().toString());
        startActivity(cameraActivityIntent);
    }

    public Size[] getSupportedResolutions() {
        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics
                    = Objects.requireNonNull(manager).getCameraCharacteristics(manager.getCameraIdList()[0]);
            StreamConfigurationMap map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            return Objects.requireNonNull(map).getOutputSizes(ImageFormat.JPEG);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void configureLogbackDirectly() {

        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.stop();

        // setup FileAppender
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(lc);
        encoder1.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder1.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(lc);
        fileAppender.setFile(this.getFileStreamPath("app.log").getAbsolutePath());
        fileAppender.setEncoder(encoder1);
        fileAppender.start();

        // setup LogcatAppender
        PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
        encoder2.setContext(lc);
        encoder2.setPattern("[%thread] %msg%n");
        encoder2.start();

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setEncoder(encoder2);
        logcatAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(fileAppender);
        root.addAppender(logcatAppender);
    }
}
