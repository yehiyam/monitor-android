package com.example.android.camera2basic;


import android.graphics.Rect;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.android.camera2basic.MainActivity.GET_MONITOR_DATA_REST;


public class SegmentsSyncer implements Runnable, SegmentsInterface {

    public static int frequency;
    private final String serverUrl;
    private final String monitorId;

    private static HashMap<String, Rect> segments;

    SegmentsSyncer(String baseUrl, String monitorId) {
        this.serverUrl = baseUrl;
        this.monitorId = monitorId;
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
        Log.d("network", "request" + request);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                String json = null;
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .registerTypeAdapter(HashMap.class, new GsonSerializations.CroppingHashMapDeSerializer())
                        .create();
                Log.d("network", "response:" + json);
//                try {
                    segments = gson.fromJson( json, new TypeToken<HashMap<String, Rect>>(){}.getType());
//                } catch ()
            }
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                getMonitorData();
                Thread.sleep(30000);
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
