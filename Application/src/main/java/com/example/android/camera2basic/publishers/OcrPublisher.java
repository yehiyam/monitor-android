package com.example.android.camera2basic.publishers;

import android.util.Log;

import com.example.android.camera2basic.Segments;
import com.example.android.camera2basic.SegmentsSyncer;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OcrPublisher extends BasePublisher{

    private final static String SUFFIX_URL = "monitor";
    private final static Gson gson = new Gson();

    private final MonitorData monitorData;

    public OcrPublisher(MonitorData monitorData, String baseUrl) {
        super(baseUrl);
        this.monitorData = monitorData;
    }
    
    @Override
    protected String getSuffixUrl() {
        return SUFFIX_URL;
    }

    @Override
    protected void setHeaders(Request.Builder builder) {

    }

    @Override
    protected Callback getCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", "onFailure: " + e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("TAG", "onResponse: " + response);
            }
        };
    }

    @Override
    protected RequestBody BuildRequestBody() {
        MediaType mediaType = MediaType.parse("application/json");
//        SegmentsSyncer.getSegments().put()
        return RequestBody.create(gson.toJson(monitorData), mediaType);
    }
}
