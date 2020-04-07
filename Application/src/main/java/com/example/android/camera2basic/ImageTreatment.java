package com.example.android.camera2basic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.os.AsyncTask;
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

import static com.example.android.camera2basic.App.OCR_FILES_DIR;
import static com.example.android.camera2basic.App.staticLogger;

public class ImageTreatment {

    public static int DOT_LOG_OCR;
    // this variable determine if
//    private int logOcrImageId;

    private final TextRecognizer textRecognizer;
//    private final Activity mActivity;
    private final UiHandler uiHandler;

    ImageTreatment(TextRecognizer textRecognizer, UiHandler uiHandler)
    {
        this.textRecognizer = textRecognizer;
        this.uiHandler = uiHandler;
//        logOcrImageId = 0;
    }

//    public int getLogOcrImageId() {
//        return logOcrImageId;
//    }
//
//    public void setLogOcrImageId(int logOcrImageId) {
//        this.logOcrImageId = logOcrImageId;
//    }

    public boolean isOperational() {
        if (textRecognizer == null) {
            return false;
        }
        return textRecognizer.isOperational();
    }

    public Segments getMeasurement(Bitmap bitmap, Rect rect, int logOcrImageId) {

        Bitmap croppedPart = crop(bitmap, rect);
        if (croppedPart == null) {
            uiHandler.showToast("bad segments - need to be reconfigured");
        }
        String measurement = RecognizeText(croppedPart);
        Segments segment = new Segments(rect);

        if (measurement == null) {
            segment.setScore(Segments.SCORE_FAILED);
        } else {
            segment.setScore(Segments.SCORE_SUCCEED);
        }

        if (logOcrImageId != 0) {
            File file = new File(
                    OCR_FILES_DIR + "/" + logOcrImageId
                            + "/" + rect.toString() + ".jpg");
            saveImage(croppedPart, file);
        }

        segment.setValue(measurement);
        return segment;
    }

    public ArrayList<Segments> getAllMeasurement(byte[] bytes, HashMap<String, Rect> croppingMap, int logOcrImageId)
    {
        if (croppingMap == null) {
            return null;
        }

        Bitmap bitmap = convertByteArrayToBitmap(bytes);

        if (logOcrImageId != 0) {
            File file = new File(
                    OCR_FILES_DIR + "/" + logOcrImageId);

            // create the directory named like the image id
            try{
                file.mkdirs();
                file  = new File(file, logOcrImageId + "-full.jpg");
                saveImage(bitmap, file);
            } catch (Exception e) {
                staticLogger.error("error while trying to save ocr log for image id" + logOcrImageId);
            }


            saveImage(bitmap, file);
        }

        ArrayList<Segments> segments = new ArrayList<>();
        for (Map.Entry<String, Rect> entry : croppingMap.entrySet()) {
            Segments segment = getMeasurement(bitmap, entry.getValue(), logOcrImageId);
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



    private Bitmap crop(Bitmap bitmap, Rect rect) {

        try {
            Log.d(getClass().getSimpleName(), rect.toString());
            bitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
        } catch (IllegalArgumentException e) {
            staticLogger.error("try to crop part which is out of image", e);
            return null;
        }

        return bitmap;
    }


    private String RecognizeText(Bitmap bitmap) {
        try {
            Frame.Builder f = new Frame.Builder();
            f.setBitmap(bitmap);
            SparseArray<TextBlock> dataReco = null;
            dataReco = textRecognizer.detect(f.build());

            // todo put null or empty
            if (dataReco == null || dataReco.size() != 1) {
                return null;
            }

            String ocrValue = dataReco.valueAt(0).getValue();
            Log.d(getClass().getSimpleName(), ocrValue);
            return ocrValue;

        }
        catch (Exception e)
        {
            staticLogger.error("got exception", e);
        }

        return null;

    }


    //todo: check that all the thread not killing the system
    private void saveImage(final Bitmap bmp, final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try (FileOutputStream out = new FileOutputStream(file)) {
                    bmp.compress(Bitmap.CompressFormat.PNG, 1, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (IOException e) {
                    staticLogger.error("can not save file: " + file.getAbsolutePath(), e);
                }
            }
        }).start();
    }
}
