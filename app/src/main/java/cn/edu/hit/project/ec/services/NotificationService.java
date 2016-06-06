package cn.edu.hit.project.ec.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import cn.edu.hit.project.ec.App;
import cn.edu.hit.project.ec.OverviewActivity;
import cn.edu.hit.project.ec.R;
import cn.edu.hit.project.ec.models.notifications.AlertNotificationData;
import cn.edu.hit.project.ec.models.notifications.NotificationData;

public class NotificationService extends Service implements SubscriptionEventListener {
    public final static int NOTIFICATION_ALERT_FALL = 1;

    private Pusher pusher;
    private NotificationManager mNotificationManager;

    public NotificationService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        pusher = new Pusher("24737bc4b88e96c1898c");
        Channel channel = pusher.subscribe("alert_channel");
        channel.bind("new_alert", this);
        pusher.connect();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pusher.disconnect();
        pusher = null;
    }

    @Override
    public void onEvent(String channelName, String eventName, String data) {
        int notifyId = 0;
        Gson gson = new Gson();
        NotificationData nData = gson.fromJson(data, NotificationData.class);
        if (nData == null || nData.sensorId != ((App) getApplicationContext()).getSensorId()) {
            return;
        }

        Intent resultIntent = new Intent(this, OverviewActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);
        if (channelName.equals("alert_channel")) {
            AlertNotificationData alert = gson.fromJson(data, AlertNotificationData.class);
            mBuilder.setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.title_alert));
            if (alert != null) {
                if (alert.type != null) {
                    if (alert.type.equals("fall")) {
                        notifyId = NOTIFICATION_ALERT_FALL;
                        mBuilder.setContentText(getString(R.string.alert_fall));
                    }
                }
            }
        }
        mNotificationManager.notify(notifyId, mBuilder.build());
    }
}
