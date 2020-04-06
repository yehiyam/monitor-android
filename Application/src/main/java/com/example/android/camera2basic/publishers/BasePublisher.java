package com.example.android.camera2basic.publishers;

import android.util.Log;
import android.widget.Toast;

import com.example.android.camera2basic.CameraActivity;
import com.example.android.camera2basic.UiHandler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.android.camera2basic.App.staticLogger;

public abstract class BasePublisher implements Runnable {

    protected static final String IMAGE_ID_KEY = "X-IMAGE-ID";
    protected static final String TIME_STAMP_KEY = "X-TIMESTAMP";
    protected static final String MONITOR_ID_KEY = "X-MONITOR-ID";

    private final String requestUrl;
    protected UiHandler uiHandler = null;
    protected int imageId;
    protected long timeStamp;
    protected String monitorId;

    BasePublisher(int imageId, String monitorId, String baseUrl, UiHandler uiHandler) {
        this.imageId = imageId;
        this.monitorId = monitorId;
        this.requestUrl = baseUrl + "/" + getSuffixUrl();
        this.uiHandler = uiHandler;
    }

    BasePublisher(String baseUrl, UiHandler uiHandler) {
        this.uiHandler = uiHandler;
        this.requestUrl = baseUrl + "/" + getSuffixUrl();
    }

    protected abstract String getSuffixUrl();

    protected abstract void setHeaders (Request.Builder builder);

    protected abstract Callback getCallback();

    protected abstract RequestBody BuildRequestBody();

    private Request buildPostRequest() {
        RequestBody body = BuildRequestBody();
        Request.Builder requestBuilder = new Request.Builder()
                .url(requestUrl)
                .method("POST", body);

        setHeaders(requestBuilder);

        return requestBuilder.build();
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = buildPostRequest();
        Log.d(getClass().getSimpleName(), "send request: " + request);
        client.newCall(request).enqueue(getCallback());
    }
}
