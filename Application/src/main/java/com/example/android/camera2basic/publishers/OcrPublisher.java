package com.example.android.camera2basic.publishers;

import android.util.Log;

import com.example.android.camera2basic.Segments;
import com.example.android.camera2basic.SegmentsSyncer;
import com.example.android.camera2basic.UiHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

import static com.example.android.camera2basic.App.staticLogger;


public class OcrPublisher extends BasePublisher{

    private final static String SUFFIX_URL = "monitor_data";
    private final static Gson gson = new GsonBuilder().serializeNulls().create();

    private final MonitorData monitorData;

    public OcrPublisher(MonitorData monitorData, int imageId, String monitorId, String baseUrl, UiHandler uiHandler) {
        super(imageId, monitorId, baseUrl, uiHandler);
        this.monitorData = monitorData;
        this.monitorData.setImageId(imageId);
        this.monitorData.setMonitorId(monitorId);
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
                if (e instanceof java.net.SocketTimeoutException) {
                    staticLogger.info("timeout: " + call.request().header(IMAGE_ID_KEY));
                    uiHandler.showToast(String.format("%s - ocr - timeout ", imageId));

                } else if (e instanceof java.net.ConnectException) {
                    staticLogger.info("connection exception: " + call.request().header(IMAGE_ID_KEY));
                    uiHandler.showToast(String.format("%s - ocr - connection exception", imageId));
                } else {
                    staticLogger.error("network problem: " + call.request().header(IMAGE_ID_KEY), e);
                    uiHandler.showToast(String.format("%s - ocr - network problem", imageId));
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    staticLogger.info(String.format("%s - ocr - response code %s: %s",
                            imageId,
                            response.code(),
                            response.body().string()
                    ));
                    uiHandler.showToast(String.format("%s - ocr - response code %s", imageId, response.code()));
                }
            }
        };
    }

    @Override
    protected RequestBody BuildRequestBody() {
        MediaType mediaType = MediaType.parse("application/json");
        String json = gson.toJson(monitorData);
        Log.d("json", "json" + json);
        return RequestBody.create(gson.toJson(monitorData), mediaType);
    }
}
