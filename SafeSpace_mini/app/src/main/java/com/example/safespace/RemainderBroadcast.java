package com.example.safespace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RemainderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"my notification");
        builder.setSmallIcon(R.drawable.ic_baseline_event_note_24);
        builder.setContentTitle("Get Things done. ");
        builder.setContentText("Check your Task Today");
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(context);
        managerCompat.notify(1,builder.build());

    }
}