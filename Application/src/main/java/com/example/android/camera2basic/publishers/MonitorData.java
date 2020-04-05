package com.example.android.camera2basic.publishers;

import com.example.android.camera2basic.Segments;

import java.util.ArrayList;

public class MonitorData {

    private int imageId;
    private String monitorId;
    private long timestamp;

    private ArrayList<Segments> segments;

    public MonitorData(ArrayList<Segments> segments, int imageId, String monitorId, long timestamp) {
        this.segments = segments;
        this.imageId = imageId;
        this.monitorId = monitorId;
        this.timestamp = timestamp;
    }

}
