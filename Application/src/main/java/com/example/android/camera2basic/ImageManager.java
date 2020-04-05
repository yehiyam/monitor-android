package com.example.android.camera2basic;

import android.graphics.Rect;
import android.media.Image;
import android.os.Handler;
import android.util.Log;

import com.example.android.camera2basic.publishers.ImagePublisher;
import com.example.android.camera2basic.publishers.MonitorData;
import com.example.android.camera2basic.publishers.OcrPublisher;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.android.camera2basic.App.staticLogger;

public class ImageManager implements Runnable {

    private final Handler backgroundHandler;
    private final int imageId;
    private final String monitorId;
    private final String baseUrl;
    private final HashMap<String, Rect> croppingMap;
    //    private final CameraActivity activity;
    private Image image;
    private ImageTreatment imageTreatment;

    ImageManager(Image image, ImageTreatment imageTreatment, android.os.Handler backgroundHandler,
                 int imageId, String monitorId, String baseUrl, HashMap<String, Rect> croppingMap) {
        this.image = image;
        this.imageTreatment = imageTreatment;
        this.backgroundHandler = backgroundHandler;
        this.imageId = imageId;
        this.monitorId = monitorId;
        this.baseUrl = baseUrl;
        this.croppingMap = croppingMap;
//        this.activity = activity;
    }


    @Override
    public void run() {
        byte[] bytes = imageTreatment.convertImageToByteArray(image);
        image.close();



        if (bytes == null) {
            staticLogger.info("did not take picture");
            return;
        }

        long timestamp = System.currentTimeMillis();

        if (imageTreatment.isOperational()) {
            ArrayList<Segments> segments = imageTreatment.getAllMeasurement(bytes, croppingMap);
            if (segments != null) {
                MonitorData monitorData = new MonitorData(segments, imageId, monitorId, timestamp);
                backgroundHandler.post(new OcrPublisher(monitorData, baseUrl));
            }
            staticLogger.info("segments" + segments);
        } else {
            staticLogger.info("ocr is not supported");
        }


        backgroundHandler.post(new ImagePublisher(bytes, imageId, monitorId, timestamp, baseUrl));
    }



}
