package com.example.android.camera2basic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity {

    public static final String IMAGE_RESOLUTION_INDEX = "IMAGE_RESOLUTION_INDEX";
    public static final String IMAGE_RESOLUTION_STRING = "IMAGE_RESOLUTION_STRING";
    public static final String SERVER_URL_STRING = "SERVER_URL_STRING";
    public static final String IMAGE_FREQUENCY_KEY = "IMAGE_FREQUENCY";
    public static final String MONITOR_ID_KEY = "MONITOR_ID";

    public static final int WRONG_QR_RESULT_CODE = 2;

    public static final int QR_ACTIVITY_REQUEST_CODE = 1;
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

    private String monitorId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyHavePermission()) {
                requestForSpecificPermission();
            }
        }




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

        monitorId = preference.getString(MONITOR_ID_KEY, null);

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

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, 101);
    }

    private boolean checkIfAlreadyHavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private  void scanQr() {
        Intent intent = new Intent(this, QrScanActivity.class);
        startActivityForResult(intent, QR_ACTIVITY_REQUEST_CODE);
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

        if (monitorId == null) {
            Toast.makeText(this, "לא הוגדר מכשיר, אנא הגדר מכשיר", Toast.LENGTH_LONG).show();
            return;
        }

        Intent cameraActivityIntent = new Intent(this, CameraActivity.class);
        cameraActivityIntent.putExtra(IMAGE_RESOLUTION_INDEX, resolutionViewModel.getResolutionIndex().getValue());
        cameraActivityIntent.putExtra(IMAGE_RESOLUTION_STRING, supportedResolution[resolutionViewModel.getResolutionIndex().getValue()].toString());
        cameraActivityIntent.putExtra(SERVER_URL_STRING, serverUrlEt.getText().toString());

        int imageFrequency = Integer.parseInt(imageFrequencyEt.getText().toString());
        preference.edit().putInt(IMAGE_FREQUENCY_KEY, imageFrequency).apply();
        cameraActivityIntent.putExtra(IMAGE_FREQUENCY_KEY, imageFrequency);
        cameraActivityIntent.putExtra(MONITOR_ID_KEY, monitorId);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            monitorId = data.getStringExtra(MONITOR_ID_KEY);
            preference.edit().putString(MONITOR_ID_KEY, monitorId).apply();
        }
    }
}
