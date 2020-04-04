package com.example.android.camera2basic;

import android.app.Activity;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.android.camera2basic.publishers.ImagePublisher;
import com.example.android.camera2basic.publishers.OcrPublisher;

import java.util.HashMap;

public class ImageManager implements Runnable {

    private final Handler backgroundHandler;
    private final int imageId;
    private final String monitorId;
    private final String baseUrl;
    private ImageReader reader;
    private ImageTreatment imageTreatment;

    ImageManager(ImageReader reader, ImageTreatment imageTreatment, android.os.Handler backgroundHandler,
                 int imageId, String monitorId, String baseUrl) {
        this.reader = reader;
        this.imageTreatment = imageTreatment;
        this.backgroundHandler = backgroundHandler;
        this.imageId = imageId;
        this.monitorId = monitorId;
        this.baseUrl = baseUrl;
    }


    @Override
    public void run() {
        byte[] bytes = imageTreatment.convertImageReaderToByteArray(reader);
        HashMap<String, String> measurements = imageTreatment.getAllMeasurement(bytes);
        if (measurements != null) {
            backgroundHandler.post(new OcrPublisher(measurements, imageId, monitorId, baseUrl));
        }
//        Log.d("measurments", );


        backgroundHandler.post(new ImagePublisher(bytes, imageId, monitorId, baseUrl));
    }



}
