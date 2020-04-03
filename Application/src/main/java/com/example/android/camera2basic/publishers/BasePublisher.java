package com.example.android.camera2basic.publishers;

import android.util.Log;
import android.widget.Toast;

import com.example.android.camera2basic.CameraActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BasePublisher implements Runnable {

    private static final String IMAGE_ID_KEY = "X-IMAGE-ID";
    private static final String TIME_STAMP_KEY = "X-TIMESTAMP";
    private static final String MONITOR_ID_KEY = "X-MONITOR-ID";

    private final String requestUrl;
    private int imageId;
    private long timeStamp;
    private String monitorId;

    BasePublisher(int imageId, String monitorId, String baseUrl) {
        this.imageId = imageId;
        this.monitorId = monitorId;
        this.requestUrl = baseUrl + getSuffixUrl();
    }

    protected abstract String getSuffixUrl();

    protected abstract Callback getCallback();

    protected abstract RequestBody BuildRequestBody();

    private Request buildPostRequest() {
        RequestBody body = BuildRequestBody();
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestUrl)
                .method("POST", body);

        String imageIdString = String.valueOf(imageId);
        requestBuilder.addHeader(IMAGE_ID_KEY, imageIdString);

        requestBuilder.addHeader(TIME_STAMP_KEY, String.valueOf(System.currentTimeMillis()));

        if (monitorId != null) {
            requestBuilder.addHeader(MONITOR_ID_KEY, monitorId);
        }

        return requestBuilder.build();
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = buildPostRequest();
        client.newCall(request).enqueue(getCallback());
    }
}
