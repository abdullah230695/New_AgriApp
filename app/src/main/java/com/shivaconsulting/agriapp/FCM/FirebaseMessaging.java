package com.shivaconsulting.agriapp.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shivaconsulting.agriapp.Home.MapsActivity;
import com.shivaconsulting.agriapp.R;

import java.util.ArrayList;


public class FirebaseMessaging  extends FirebaseMessagingService {
    private String TAG = "fcm";
    String title,body;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From:" + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

             title = remoteMessage.getData().get("title");
             body = remoteMessage.getData().get("body");

            sendNotification("Your Booking has Received a Notification", "Tap to View Details");
    }

        sendNotification(title, body);

        // Check if message contains a notification payload.
      /*  if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification("title", body);

        }*/
    }

    private void sendNotification(String title,String body) {

        //on background
        String channelId = "Default";
         ArrayList array= new ArrayList<String>();
         array.add(body);
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putStringArrayListExtra(channelId, array);
        intent.setAction(channelId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 300, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.combine)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setGroup(channelId)
                .setGroupSummary(true)
                .setSound(defaultSoundUri).setAutoCancel(true);
        ;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        manager.notify(0, builder.build());
    }


}
