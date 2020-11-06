package com.shivaconsulting.agriapp.FCM;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shivaconsulting.agriapp.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        getFirebaseMessage(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());

    }

    public  void  getFirebaseMessage(String title, String msg){
        title="OK";
        msg="hhjhjhhaakjakjaljlajajalk" +
                "sagksksaahahhkahkajha" +
                "sakhkjh";

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(), "Booking Notification");
        builder.setContentTitle(title).setContentText(msg).setSmallIcon(R.drawable.ic_baseline_notifications_active_24).setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(100,builder.build());
    }
}
