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

    public static final String IMAGE_ID_KEY = "X-IMAGE-ID";
    public static final String MONITOR_ID_KEY = "X-MONITOR-ID";
    public static final String TIME_STAMP_KEY = "X-TIMESTAMP";


    /**
     * The JPEG image
     */

    private final Image mImage;
    private final String mId;

    private final String SERVER_URL = "http://52.157.71.156";

    private final String UPLOAD_IMAGE_REST_FUNCTION = "monitor_image";
    private final String TAG = "Publisher";
    private final Activity mActivity;
    public String nextImageId;

    /**
     * The file we save the image into.
     * @return
     */

    public Response sendImageInPost(byte[] image, String imageId, String monitorId, String timeStamp) {
        try {
            String requestUrl = String.format("%s/%s", SERVER_URL, UPLOAD_IMAGE_REST_FUNCTION);
            Log.d(TAG, String.format("send request to: %s", requestUrl));

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("image/jpeg");
                RequestBody body = RequestBody.create(image, mediaType);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(requestUrl)
                    .method("POST", body);

            if (imageId != null) {
                requestBuilder.addHeader(IMAGE_ID_KEY, imageId);
            }

            if (monitorId != null) {
                requestBuilder.addHeader(MONITOR_ID_KEY, monitorId);
            }

            if (timeStamp != null) {
                requestBuilder.addHeader(TIME_STAMP_KEY, timeStamp);
            }

            Request request = requestBuilder.build();

            //todo add check that the response is correct
            return client.newCall(request).execute();

        } catch (IOException e) {
            Toast.makeText(mActivity, "error sending the image", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }

    Publisher(Image image, String id, Activity activity) {
        mImage = image;
        mId = id;

        //todo: yakir look on this
        mActivity = activity;
    }

//    void handleResult(Response response) {
//        response
//    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        Response response = sendImageInPost(bytes, null, null, String.valueOf(System.currentTimeMillis()));
        Log.d(TAG, "respone: " + response.toString());

        mImage.close();

//        if (response != null) {
//            try {
//                response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            nextImageId = respone.body()
//        }


        }
    }
