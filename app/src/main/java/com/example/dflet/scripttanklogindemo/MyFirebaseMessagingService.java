package com.example.dflet.scripttanklogindemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int uniqueIdNM = 55052;
    private static final String CHANNEL_ID = "55052";
    private NotificationCompat.Builder notification;
    private NotificationManager notificationManager;
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {

        String from = message.getFrom();

        System.out.println("Message recevied from: " + from);

        if (message.getData().size() > 0) {
            System.out.println(message.getData());
        }

        HashMap<String, String> notificationData;
        if (message.getNotification() != null) {
            notificationData = new HashMap<>();
            notificationData.put("title", message.getNotification().getTitle());
            notificationData.put("body", message.getNotification().getBody());
            buildNotification(notificationData);
        }
    }




    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        // TODO
    }

    private void buildNotification(HashMap<String, String> data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ScriptTalk";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_ID);
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(this, CHANNEL_ID);
        }

        notification.setPriority(NotificationCompat.PRIORITY_DEFAULT).
                setContentTitle(data.get("title")).
                setContentText(data.get("body")).setSmallIcon(R.mipmap.ic_launcher).
                setAutoCancel(true);
        Intent intent = new Intent(this, MyFirebaseMessagingService.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        notificationManager.notify(uniqueIdNM, notification.build());
    }


}
