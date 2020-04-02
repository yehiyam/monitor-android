package com.example.android.camera2basic;

import android.app.Activity;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


class Publisher implements Runnable {

    public static final String IMAGE_ID_KEY = "X-IMAGE-ID";
    public static final String TIME_STAMP_KEY = "X-TIMESTAMP";

    org.slf4j.Logger logger = LoggerFactory.getLogger(MainActivity.class);


    /**
     * The JPEG image
     */

    private final Image mImage;

    private final String UPLOAD_IMAGE_REST_FUNCTION = "monitor_image";
    private final String TAG = "Publisher";
    private final Activity mActivity;
    /**
     * The file we save the image into.
     * @return
     */

    public void sendImageInPost(byte[] image, final String imageId, String timeStamp) {
        String requestUrl = String.format("%s/%s", ((CameraActivity) mActivity).serverUrl, UPLOAD_IMAGE_REST_FUNCTION);

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

        if (timeStamp != null) {
            requestBuilder.addHeader(TIME_STAMP_KEY, timeStamp);
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, "sending image " + imageId, Toast.LENGTH_SHORT).show();
            }
        });

        final Request request = requestBuilder.build();
        logger.info(request.toString());



        //todo add check that the response is correct
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (e instanceof java.net.SocketTimeoutException) {
                    logger.info("timeout: " + call.request().header(IMAGE_ID_KEY));
                } else {
                    logger.error(e.toString());
                }
                e.printStackTrace();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, String.format("%s: did not get response", imageId) , Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logger.info(response.toString());
                        Toast.makeText(mActivity, String.format("%s: got return code: " + response.code(), imageId), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    public void saveImage(byte[] bytes) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(new File(mActivity.getExternalFilesDir(null), ((CameraActivity)mActivity).getImageId() + ".jpg"));
            output.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mImage.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Publisher(Image image, Activity activity) {
        mImage = image;
        mActivity = activity;
    }

    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        sendImageInPost(bytes, String.valueOf( ((CameraActivity) mActivity).getImageId()), String.valueOf(System.currentTimeMillis()));
        saveImage(bytes);
        ((CameraActivity) mActivity).addToImageIdCounter();
        mImage.close();
        }
    }
