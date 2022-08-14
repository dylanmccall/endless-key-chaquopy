package org.endlessos.endlesskey;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.databinding.ObservableField;

import com.chaquo.python.android.PyApplication;

public class EndlessKeyApplication extends PyApplication {
    public final ObservableField<KolibriStatus> kolibriStatus = new ObservableField<>(KolibriStatus.STOPPED);
    public final ObservableField<String> kolibriBaseUrl = new ObservableField<>();
    public final ObservableField<String> kolibriAppKey = new ObservableField<>();

    public enum KolibriStatus {
        STARTING,
        STOPPED,
        STARTED,
        ERROR
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Actions.SERVING)) {
                    Bundle extras = intent.getExtras();
                    kolibriBaseUrl.set(extras.getString("baseUrl"));
                    kolibriAppKey.set(extras.getString("appKey"));
                    kolibriStatus.set(KolibriStatus.STARTED);
                    Log.w("endless-key", String.format("Received broadcast: %s", kolibriBaseUrl.get()));
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Actions.SERVING);
        registerReceiver(broadcastReceiver, filter);

        // TODO: Start Kolibri only after the initial setup wizard.
        //       To do this, we will need to call startKolibri from any interested activities,
        //       instead of from `onCreate`.
        startKolibri();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(NotificationChannels.SERVICE_CHANNEL, getText(R.string.service_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
        }
    }

    public void startKolibri() {
        Intent intent = new Intent(this, KolibriServer.class);
        startForegroundService(intent);
    }

    public void stopKolibri() {
        // TODO: Implement this. Ask KolibriServer to stop after a timeout.
    }
}
