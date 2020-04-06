package com.example.android.camera2basic;


import android.graphics.Rect;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.android.camera2basic.App.staticLogger;


public class SegmentsSyncer implements Runnable {

    private static final String GET_MONITOR_DATA_REST = "monitor";

    public static int frequency;
    private final String serverUrl;
    private final String monitorId;

//    Handler uiHandler = new android.os.Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            Toast.makeText()
//        }
//    }

    private static HashMap<String, Rect> segments;
    private final UiHandler uiHandler;

    SegmentsSyncer(String baseUrl, String monitorId, UiHandler uiHandler) {
        this.serverUrl = baseUrl;
        this.monitorId = monitorId;
        this.uiHandler = uiHandler;
    }

    private void getMonitorData () {

        if (monitorId == null) {
            return;
        }

        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s/%s/%s",
                serverUrl, GET_MONITOR_DATA_REST, monitorId);
        final Request request = new Request.Builder()
                .url(url)
                .build();

        staticLogger.info("send request " + request);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String json = null;
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    staticLogger.error(String.format("error in response to %s", call.request()), e);
                    uiHandler.showToast(R.string.can_not_get_segments_from_server_string);
                }

                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .registerTypeAdapter(HashMap.class, new GsonSerializations.CroppingHashMapDeSerializer())
                        .create();
                try {
                    segments = gson.fromJson(json, new TypeToken<HashMap<String, Rect>>(){}.getType());
                } catch (com.google.gson.JsonSyntaxException e) {
                    staticLogger.error(String.format("got bad data from server: %s", json));
                    uiHandler.showToast("מקבל סגמנטים שגויים מהשרת");
                }
            }
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                getMonitorData();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                //log
            }
        }
    }

    //todo: remove the static
    public static HashMap<String, Rect> getSegments() {
        return segments;
    }
}
