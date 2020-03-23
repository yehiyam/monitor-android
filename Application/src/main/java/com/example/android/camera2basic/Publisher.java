package com.example.android.camera2basic;

import android.app.Activity;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


class Publisher implements Runnable {

    /**
     * The JPEG image
     */

    private final Image mImage;
    private final String mId;

    private final String SERVER_URL = "http://192.168.1.13:3000";

    private final String UPLOAD_IMAGE_REST_FUNCTION = "posts";
    private final String TAG = "Publisher";
    private final Activity mActivity;

    /**
     * The file we save the image into.
     */

    public void sendImageInPost(byte[] image) {
        try {
            String requestUrl = String.format("%s/%s?id=%s", SERVER_URL, UPLOAD_IMAGE_REST_FUNCTION, mId);
            Log.d(TAG, String.format("send request to: %s", requestUrl));


            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, image);
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            //todo add check that the response is correct
            Response response = client.newCall(request).execute();

        } catch (IOException e) {
            Toast.makeText(mActivity, "error sending the image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    Publisher(Image image, String id, Activity activity) {
        mImage = image;
        mId = id;

        //todo: yakir look on this
        mActivity = activity;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        sendImageInPost(bytes);
        }
    }
