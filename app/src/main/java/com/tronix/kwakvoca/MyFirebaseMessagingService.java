package com.tronix.kwakvoca;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        // Handle FCM messages here.

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
             sendNotification(remoteMessage);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "new-version";
            String name = "새로운 버전";

            makeNotificationChannel(id, name);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, id)
                    .setSmallIcon(R.drawable.checkbox_bookmark)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setChannelId(id)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            notifyMessage(notificationBuilder);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void makeNotificationChannel(String channelId, String channelName) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("새로운 버전 알림");
        channel.enableLights(true);
        channel.enableVibration(true);
        Objects.requireNonNull(manager).createNotificationChannel(channel);
    }

    private void notifyMessage(NotificationCompat.Builder notificationBuilder) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(manager).notify(0, notificationBuilder.build());
    }
}
