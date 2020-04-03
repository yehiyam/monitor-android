package com.example.android.camera2basic;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class NetworkManager {

    public static String GET_MONITOR_DATA_REST = "monitor";



    private final String UPLOAD_IMAGE_REST_FUNCTION = "monitor_image";
    private final String TAG = "Publisher";

//    private final Activity mActivity;

//    NetworkManager(Activity activity) {
////        mActivity = activity;
//    }



    public void getMonitorData (String serverUrl, String monitorId, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s/%s/%s",
                serverUrl, GET_MONITOR_DATA_REST, monitorId);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d(TAG, "getMonitorImage: send request" + request);
        client.newCall(request).enqueue(callback);
    }

    public void sendImageInPost(byte[] image, final String imageId, String monitorId, String timeStamp) {
        String requestUrl = String.format("%s/%s", ((CameraActivity) mActivity).serverUrl, UPLOAD_IMAGE_REST_FUNCTION);
        Log.d(TAG, "sendImageInPost: " + requestUrl);

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

        if (monitorId != null) {
            requestBuilder.addHeader(MONITOR_ID_KEY, monitorId);
        }

//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(mActivity, "sending image " + imageId, Toast.LENGTH_SHORT).show();
//            }
//        });

        final Request request = requestBuilder.build();
        Log.e(TAG, "sendImageInPost: " + request);


        //todo add check that the response is correct
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
//                mActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(mActivity, String.format("%s: did not get response", imageId), Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, String.format("%s: did not get response", imageId));
//                    }
//                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: " + response);
                        Toast.makeText(mActivity, String.format("%s: got return code: " + response.code(), imageId), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
