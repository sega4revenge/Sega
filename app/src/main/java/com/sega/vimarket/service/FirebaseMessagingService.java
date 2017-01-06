package com.sega.vimarket.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.sega.vimarket.R;
import com.sega.vimarket.activity.ProductActivity;

/**a
 * Created by Sega on 08/08/2016.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        showNotification(remoteMessage.getData().get("message"));
    }

    private void showNotification(String message) {

        Intent i = new Intent(this, ProductActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("FreeMarket")
                .setContentText(message)
                .setSmallIcon(R.drawable.logofree)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
        Intent a = new Intent();
        a.setAction("appendChatScreenMsg");
        a.putExtra("reload", true);
        this.sendBroadcast(a);
        Intent x = new Intent();
        x.setAction("productdetail");
        x.putExtra("reload", true);
        this.sendBroadcast(x);
    }




}