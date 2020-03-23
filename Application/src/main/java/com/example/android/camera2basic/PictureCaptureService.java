package com.example.android.camera2basic;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */


public class PictureCaptureService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_START_TAKING_PICURES = "com.example.android.camera2basic.action.FOO";
    private static final String ACTION_BAZ = "com.example.android.camera2basic.action.BAZ";

    public final String TAG = "pictureService";

    Camera2BasicFragment camera2BasicFragment = new Camera2BasicFragment();

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.android.camera2basic.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.android.camera2basic.extra.PARAM2";

    public int IMAGE_FREQUENCY = 100;

    private Handler handler;
    private Runnable runnable;

    public PictureCaptureService() {
        super("PcitureCaptureService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionTakingPictures(Context context) {
        Intent intent = new Intent(context, PictureCaptureService.class);
        intent.setAction(ACTION_START_TAKING_PICURES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }


    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_TAKING_PICURES.equals(action)) {
                handleActionStartTakingPictures();
                
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionStopTakingPictures(param1, param2);
            }
        }
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */


    private void handleActionStartTakingPictures() {

        Log.d(TAG, "handleActionStartTakingPictures: start service");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        }
        runnable = new Runnable() {
            public void run() {
                Log.d(TAG, "taking picture");
                camera2BasicFragment.takePicture();
                handler.postDelayed(runnable, IMAGE_FREQUENCY);
            }
        };
        handler.postDelayed(runnable, 0);


    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStopTakingPictures(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
