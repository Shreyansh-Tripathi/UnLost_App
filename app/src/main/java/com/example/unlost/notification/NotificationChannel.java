package com.example.unlost.notification;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class NotificationChannel extends Application {

    public static final String channelId= "ChannelId";
    public static final String channelName= "Channel";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        
        createNotificationChannel();
    }

    private void createNotificationChannel() {

      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
      {
          android.app.NotificationChannel channel = new android.app.NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
          channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
          channel.enableLights(true);
          channel.enableVibration(true);

          NotificationManager nMananger = getSystemService(NotificationManager.class);
          assert nMananger != null;
          nMananger.createNotificationChannel(channel);
      }
    }
}