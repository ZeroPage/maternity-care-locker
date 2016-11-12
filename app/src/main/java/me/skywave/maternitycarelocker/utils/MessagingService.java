package me.skywave.maternitycarelocker.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import me.skywave.maternitycarelocker.companion.TimerActivity;

public class MessagingService extends FirebaseMessagingService{
    Context context = getApplicationContext();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, TimerActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        RemoteMessage.Notification remoteNoti = remoteMessage.getNotification();
        builder.setSmallIcon(android.R.drawable.ic_notification_clear_all);
        builder.setContentTitle(remoteNoti.getTitle());
        builder.setContentText(remoteNoti.getBody());
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notiManager.notify(001, builder.build());
    }
}
