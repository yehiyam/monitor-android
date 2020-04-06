package com.example.android.camera2basic.publishers;

import android.app.Activity;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import com.example.android.camera2basic.CameraActivity;
import com.example.android.camera2basic.UiHandler;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.android.camera2basic.App.staticLogger;


public class ImagePublisher extends BasePublisher {

    /**
     * The JPEG image
     */

    private final String SUFFIX_URL = "monitor_image";
    private final String TAG = "Publisher";
    private final byte[] image;

    /**
     * The file we save the image into.
     *
     * @return
     */

    public ImagePublisher(byte[] image, int imageId, String monitorId, long timestamp, String BaseUrl, UiHandler uiHandler) {
        super(imageId, monitorId, BaseUrl, uiHandler);
        this.image = image;
        this.timeStamp = timestamp;
    }

    @Override
    protected String getSuffixUrl() {
        return SUFFIX_URL;
    }

    @Override
    protected void setHeaders(Request.Builder builder) {
        String imageIdString = String.valueOf(imageId);
        builder.addHeader(IMAGE_ID_KEY, imageIdString);

        builder.addHeader(TIME_STAMP_KEY, String.valueOf(timeStamp));

        if (monitorId != null) {
            builder.addHeader(MONITOR_ID_KEY, monitorId);
        }
    }

    @Override
    protected Callback getCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (e instanceof java.net.SocketTimeoutException) {
                    staticLogger.info("timeout: " + call.request().header(IMAGE_ID_KEY));
                    uiHandler.showToast(String.format("%s - image -timeout ", imageId));

                } else if (e instanceof java.net.ConnectException) {
                    staticLogger.info("connection exception: " + call.request().header(IMAGE_ID_KEY));
                    uiHandler.showToast(String.format("%s - image - connection exception", imageId));
                } else {
                    staticLogger.error("network problem: " + call.request().header(IMAGE_ID_KEY), e);
                    uiHandler.showToast(String.format("%s - image - network problem", imageId));
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    staticLogger.info(String.format("%s got response code %s: %s",
                            imageId,
                            response.code(),
                            response.body().string()
                            ));
                    uiHandler.showToast(String.format("%s - response code %s", imageId, response.code()));
                }
            }
        };
    }

    @Override
    protected RequestBody BuildRequestBody() {
        MediaType mediaType = MediaType.parse("image/jpeg");
        return RequestBody.create(image, mediaType);
    }
}