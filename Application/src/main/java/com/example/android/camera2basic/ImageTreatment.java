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
import java.util.HashMap;
import java.util.Map;

public class ImageTreatment {

    private final HashMap<String, Rect> croppingMap;
    private final TextRecognizer textRecognizer;
    private final Activity mActivity;
    Bitmap bitmap;

    ImageTreatment(HashMap<String, Rect> croppingMap, TextRecognizer textRecognizer, Activity activity)
    {
        this.croppingMap = croppingMap;
        this.textRecognizer = textRecognizer;
        mActivity = activity;
    }

    public String getMeasurement(Bitmap bitmap, Rect rect) {

        Bitmap croppedPart = Crop(bitmap, rect);
        String measurement = RecognizeText(croppedPart);
        return measurement;
    }

    public HashMap<String, String> getAllMeasurement(byte[] bytes)
    {
        if (croppingMap == null) {
            return null;
        }

        Bitmap bitmap = convertByteArrayToBitmap(bytes);
//        File f = new File(mActivity.getExternalFilesDir(null), "test.jpg");
//        bitmap =  BitmapFactory.decodeFile(f.getAbsolutePath());
//        String dataReco = RecognizeText(bitmap);
//        RecognizeText(bitmap);
        HashMap<String, String> measurementHash = new HashMap<>();
        for (Map.Entry<String, Rect> entry : croppingMap.entrySet()) {
            measurementHash.put(entry.getKey(), getMeasurement(bitmap, entry.getValue()));
        }

        return measurementHash;
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
