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
    private final UiHandler uiHandler;
    //    private final CameraActivity activity;
    private Image image;
    private ImageTreatment imageTreatment;

    ImageManager(Image image, ImageTreatment imageTreatment, android.os.Handler backgroundHandler,
                 int imageId, String monitorId, String baseUrl, HashMap<String, Rect> croppingMap, UiHandler uiHandler) {
        this.image = image;
        this.imageTreatment = imageTreatment;
        this.backgroundHandler = backgroundHandler;
        this.imageId = imageId;
        this.monitorId = monitorId;
        this.baseUrl = baseUrl;
        this.croppingMap = croppingMap;
        this.uiHandler = uiHandler;
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
        Log.d("imageid", "run: " + imageId);
        uiHandler.showToast("sending " + imageId);


        if (imageTreatment.isOperational()) {

//            if (CameraActivity.getNumberOfImagesPerSaving() != null && imageId % CameraActivity.getNumberOfImagesPerSaving() == 0) {
//                imageTreatment.setLogOcrImageId(imageId);
//            }

            int ocrLogImageId = (CameraActivity.getNumberOfImagesPerSaving() != null && imageId % CameraActivity.getNumberOfImagesPerSaving() == 0) ? imageId : 0;

            ArrayList<Segments> segments = imageTreatment.getAllMeasurement(bytes, croppingMap, ocrLogImageId);


            if (segments != null) {
                MonitorData monitorData = new MonitorData(segments, timestamp);
//                backgroundHandler.post(new OcrPublisher(monitorData, imageId, monitorId, baseUrl, uiHandler));
                try {
                    new OcrPublisher(monitorData, imageId, monitorId, baseUrl, uiHandler).run();
                }
                catch (Exception ex){
                    staticLogger.warn("sending ocr failed" + ex.getMessage());
                }

            }

//            if (CameraActivity.getNumberOfImagesPerSaving() != null && imageId % CameraActivity.getNumberOfImagesPerSaving() == 0) {
//                imageTreatment.setLogOcrImageId(ImageTreatment.DOT_LOG_OCR);
//            }

        } else {
            uiHandler.showToast("ocr is not supported");
            staticLogger.info("ocr is not supported");
        }
        try {
            new ImagePublisher(bytes, imageId, monitorId, timestamp, baseUrl, uiHandler).run();
        }
        catch (Exception ex){
            staticLogger.warn("sending image failed" + ex.getMessage());
        }
//        backgroundHandler.post(new ImagePublisher(bytes, imageId, monitorId, timestamp, baseUrl, uiHandler));
    }
}
