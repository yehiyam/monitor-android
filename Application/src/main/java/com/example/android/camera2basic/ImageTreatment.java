package com.example.android.camera2basic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageTreatment {

    private final TextRecognizer textRecognizer;
    private final Activity mActivity;
    Bitmap bitmap;

    ImageTreatment(TextRecognizer textRecognizer, Activity activity)
    {
        this.textRecognizer = textRecognizer;
        mActivity = activity;
    }

    public boolean isOperational() {
        if (textRecognizer == null) {
            return false;
        }
        return textRecognizer.isOperational();
    }

    public Segments getMeasurement(Bitmap bitmap, Rect rect) {

        Bitmap croppedPart = Crop(bitmap, rect);
        String measurement = RecognizeText(croppedPart);
        Segments segment = new Segments(rect);

        if (measurement == null) {
            segment.setScore(Segments.SCORE_FAILED);
        } else {
            segment.setScore(Segments.SCORE_SUCCEED);
        }

        segment.setValue(measurement);
        return segment;
    }

    public ArrayList<Segments> getAllMeasurement(byte[] bytes, HashMap<String, Rect> croppingMap)
    {
        if (croppingMap == null) {
            return null;
        }

        Bitmap bitmap = convertByteArrayToBitmap(bytes);
//        File f = new File(mActivity.getExternalFilesDir(null), "test.jpg");
//        bitmap =  BitmapFactory.decodeFile(f.getAbsolutePath());
//        String dataReco = RecognizeText(bitmap);
//        RecognizeText(bitmap);
        ArrayList<Segments> segments = new ArrayList<>();
        for (Map.Entry<String, Rect> entry : croppingMap.entrySet()) {
            Segments segment = getMeasurement(bitmap, entry.getValue());
            segment.setName(entry.getKey());
            segments.add(segment);
        }

        return segments;
    }

    private Bitmap convertByteArrayToBitmap(byte[] bytes)
    {

        if (bytes == null) {
            return null;
        }

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        }
        catch (Exception e)
        {
            //Log
            return null;
        }
    }

    public byte[] convertImageToByteArray(Image image) {

        if (image == null) {
            return null;
        }

        try {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }



    private Bitmap Crop(Bitmap bitmap, Rect rect) {

//        try {
        Log.d("", "Crop: " + rect);
            bitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
//        } catch (IllegalArgumentException e) {
//
//        }
        saveImage(bitmap);
        return bitmap;
    }


    private String RecognizeText(Bitmap bitmap) {
        try {
            Frame.Builder f = new Frame.Builder();
            f.setBitmap(bitmap);
            SparseArray<TextBlock> dataReco = null;
            dataReco = textRecognizer.detect(f.build());
            Log.d("textRecognizer", "RecognizeText: " + dataReco);
            // todo put null or empty
            if (dataReco == null || dataReco.size() != 1) {
                return null;
            }

            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0;i<dataReco.size();i++){
                TextBlock item = dataReco.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }

            return stringBuilder.toString();
        }
        catch (Exception e)
        {
            //lOG
        }

        return null;

    }

    public void saveImage(Bitmap bmp) {
        FileOutputStream output = null;
        File filename = new File(mActivity.getExternalFilesDir("crop"), System.currentTimeMillis() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(filename)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 1, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
