package com.example.android.camera2basic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final String IMAGE_RESOLUTION_INDEX = "IMAGE_RESOLUTION_INDEX";
    public static final String IMAGE_RESOLUTION_STRING = "IMAGE_RESOLUTION_STRING";
    public static final String SERVER_URL_STRING = "SERVER_URL_STRING";
    public static final String IMAGE_FREQUENCY_KEY = "IMAGE_FREQUENCY";
    public static final String MONITOR_ID_KEY = "MONITOR_ID";
    public static final int IMAGE_FREQUENCY_DEFAULT_MILI = 2000;

    ZXingScannerView mScannerView;

    Button resolutionPlus;
    Button resolutionMinus;
    Button scanQrButton;

    TextView currentResolution;
    EditText serverUrlEt;
    EditText imageFrequencyEt;

    ResolutionViewModel resolutionViewModel;

    private Size imageResolution;

    public Size[] supportedResolution;
    private SharedPreferences preference;
    private String image_resolution_index;
    ViewGroup view;



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
        scanQrButton = findViewById(R.id.scan_qr_bt);
        currentResolution = findViewById(R.id.resolution_string_tv);

        serverUrlEt = findViewById(R.id.server_url_et);
        serverUrlEt.setText(R.string.default_server_url);

        imageFrequencyEt = findViewById(R.id.image_frequncy_et);

        Integer imageFrequency = preference.getInt(IMAGE_FREQUENCY_KEY, IMAGE_FREQUENCY_DEFAULT_MILI);
        imageFrequencyEt.setText(imageFrequency.toString());


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

        mScannerView = new ZXingScannerView(this);

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

        scanQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQr();
            }
        });

    }

    private  void scanQr() {
        view = (ViewGroup)getWindow().getDecorView();
        setContentView(mScannerView);
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

        int imageFrequency = Integer.parseInt(imageFrequencyEt.getText().toString());
        preference.edit().putInt(IMAGE_FREQUENCY_KEY, imageFrequency).apply();
        cameraActivityIntent.putExtra(IMAGE_FREQUENCY_KEY, imageFrequency);

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

    private void handleWrongQr() {
        Toast.makeText(this, "wrong qr", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleResult(Result result) {
        setContentView(view);
//        setContentView(R.layout.activity_main);
        String monitorId = result.getText();

        if  (!monitorId.startsWith(getString(R.string.qr_monitor_prefix))) {
            handleWrongQr();
        } else {
            Toast.makeText(this, monitorId, Toast.LENGTH_SHORT);
            preference.edit().putString(MONITOR_ID_KEY, monitorId).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pa// use
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
//        View view = super.onCreateView(name, context, attrs);
        View view =
        view = View.inflate()
    }

    public View setUpView() {

    }

}
