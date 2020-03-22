package com.example.android.camera2basic;

import android.media.Image;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.nio.ByteBuffer;
import java.util.Arrays;


class Publisher implements Runnable {

    /**
     * The JPEG image
     */
    private final Image mImage;
    private final String mId;

    private final String SERVER_URL = "http://localhost:3000";

    private final String UPLOAD_IMAGE_REST_FUNCTION = "upload";
    private final String TAG = "Publisher";

    /**
     * The file we save the image into.
     */

    public void sendImageInPost(byte[] image) {
        try {
//            Log.d(TAG, "send request ");
            HttpResponse<String> response = Unirest.post(String.format("%s/%s?id=%s", SERVER_URL, UPLOAD_IMAGE_REST_FUNCTION, mId))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(Arrays.toString(image))
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    Publisher(Image image, String id) {
        mImage = image;
        mId = id;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        sendImageInPost(bytes);
        }
    }
