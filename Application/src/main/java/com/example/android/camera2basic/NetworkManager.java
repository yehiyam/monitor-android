package com.example.android.camera2basic;

import android.graphics.Rect;
import android.util.Log;

import java.util.HashMap;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.ContentValues.TAG;

public class NetworkManager {

    public static String GET_MONITOR_DATA_REST = "monitor";

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
}
