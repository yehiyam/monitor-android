package com.example.android.camera2basic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
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

    public HashMap<String, String> getAllMeasurement(ImageReader reader)
    {
        Bitmap bitmap = convertImageReaderToBitmap(reader);
        HashMap<String, String> measurementHash = new HashMap<>();
        for (Map.Entry<String, Rect> entry : croppingMap.entrySet()) {
            measurementHash.put(entry.getKey(), getMeasurement(bitmap, entry.getValue()));
        }

        return measurementHash;
    }

    private Bitmap convertImageReaderToBitmap(ImageReader reader)
    {
        try {
            Image image = reader.acquireLatestImage();

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        }
        catch (Exception e)
        {
            //Log
            return null;
        }
    }


    private Bitmap Crop(Bitmap bitmap, Rect rect) {
        bitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
        saveImage(bitmap);
        return bitmap;
    }


    private String RecognizeText(Bitmap bitmap) {
        try {
            Frame.Builder f = new Frame.Builder();
            f.setBitmap(bitmap);

            SparseArray<TextBlock> dataReco = textRecognizer.detect(f.build());

            // todo put null or empty
            if (dataReco == null || dataReco.size() != 1) {
                return null;
            }
            return dataReco.get(0).toString();

//            StringBuilder stringBuilder = new StringBuilder();
//            for(int i=0;i<dataReco.size();i++){
//                TextBlock item = dataReco.valueAt(i);
//                stringBuilder.append(item.getValue());
//                stringBuilder.append("\n");
//            }
//
//            return stringBuilder;
        }
        catch (Exception e)
        {
            //lOG
        }

        return null;

    }

    public void saveImage(Bitmap bmp) {
        FileOutputStream output = null;
        File filename = new File(mActivity.getExternalFilesDir(null), ((CameraActivity) mActivity).getImageId() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(filename)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 1, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
