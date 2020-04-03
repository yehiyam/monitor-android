package com.example.android.camera2basic;

import android.app.Activity;
import android.media.ImageReader;

import java.util.HashMap;

public class OcrPublishRunnable implements Runnable {

    private ImageReader reader;
    private NetworkManager networkManager;
    private ImageTreatment imageTreatment;
    Activity activity;

    OcrPublishRunnable(ImageReader reader, ImageTreatment imageTreatment) {
        this.reader = reader;
        this.imageTreatment = imageTreatment;
    }


    @Override
    public void run() {
        byte[] bytes = imageTreatment.convertImageReaderToByteArray(reader);
        HashMap<String, String> measurements = imageTreatment.getAllMeasurement(bytes);


//        networkManager = new NetworkManager(activity);



        networkManager.sendImageInPost();
        //send with network manager

    }



}
