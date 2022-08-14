package org.endlessos.endlesskey;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class KolibriServer extends IntentService {
    public KolibriServer() {
        super("KolibriServer");
    }

    private static final int SERVICE_NOTIFICATION_ID = 1000;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        startForeground(SERVICE_NOTIFICATION_ID, buildServiceNotification());

        Log.d("endless-key", String.format("onHandleIntent: %s", intent.toString()));
        Log.d("endless-key", String.format("Is Python running: %s", Python.isStarted()));
        PyObject kolibriServerMain = Python.getInstance().getModule("kolibri_server_main");
        PyObject result = kolibriServerMain.callAttr("main", this.getApplicationContext());
    }

    private Notification buildServiceNotification() {
        Intent notificationIntent = new Intent(this, KolibriServer.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Builder(this, NotificationChannels.SERVICE_CHANNEL)
                .setContentTitle(getText(R.string.service_notification_title))
                // .setContentText(getText(R.string.service_notification_message)
                // .setTicker(getText(R.string.service_notification_ticker))
                // .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .build();
    }

    // TODO: Provide a callback for the Python application so we can broadcast
    //       org.endlessos.endlesskey.SERVING here.
}