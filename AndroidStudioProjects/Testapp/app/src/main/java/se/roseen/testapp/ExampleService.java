package se.roseen.testapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExampleService extends Service implements LocationListener {


    //private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    //private int NOTIFICATION = R.string.local_service_started;

    public static final String TAG = "---ExampleService---";
    public static final long MAX_TIME_SEC = TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES);
    public static final int DELAY_INITIAL = 3;
    public static final int DELAY_RECURRING = 6;
    public static final TimeUnit DELAY_UNIT = TimeUnit.SECONDS;
    private ScheduledExecutorService mExecutor;

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "onProviderDisabled");
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        ExampleService getService() {
            return ExampleService.this;
        }
    }

    @Override
    public void onCreate() {
        //mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);

        mExecutor = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        recurringTask();
                    }
                };
        mExecutor.scheduleWithFixedDelay(runnable, DELAY_INITIAL, DELAY_RECURRING, DELAY_UNIT);

        Log.d(TAG, "Scheduled runnable");

        return START_STICKY;
    }

    private void recurringTask() {

        Log.d(TAG, "Recurring task...");

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                Log.d(TAG, "Requested a single update");
            } else {
                String str = "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude();
                Log.d(TAG, str);
//                Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
                //postToastMessage(str);
            }

        } catch (SecurityException se) {
            Log.e(TAG, "Security Exception!");
        } catch (Exception ex) {
            Log.e(TAG, "ERROR");
            Log.e(TAG, ex.getMessage());
            Log.e(TAG, "ERROR");
        }
    }
/*
    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ExampleService.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
*/
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
//        mNM.cancel(NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(notificationId);

        // Tell the user we stopped.
        //Toast.makeText(this, "ExampleService Stopped.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */

    private int notificationId = 4711;

    private void showNotification() {
        Log.i(TAG, "ExampleService Running.");

        CharSequence text = "ExampleService Running";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getText(R.string.example_service_label))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());

        /*
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "ExampleService Started.";

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LocalServiceActivities.Controller.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.stat_sample)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
        */
    }
}
