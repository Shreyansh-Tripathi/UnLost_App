package com.example.unlost.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.unlost.R;

import com.example.unlost.activities.Lost_and_Found_activity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.unlost.notification.NotificationChannel.channelId;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String title,message;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title=remoteMessage.getData().get("title");
        message=remoteMessage.getData().get("message");

        Intent intent = new Intent(this, Lost_and_Found_activity.class);
        PendingIntent gotoapp= PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setColor(Color.BLUE)
                .setSmallIcon(R.drawable.search_icon)
                .setContentIntent(gotoapp)
                .setAutoCancel(true);

        NotificationManagerCompat  manager= NotificationManagerCompat.from(getApplicationContext());
        manager.notify(101, notification.build());
    }
}
