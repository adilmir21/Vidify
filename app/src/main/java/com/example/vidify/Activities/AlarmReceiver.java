package com.example.vidify.Activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.vidify.R;
import com.example.vidify.Utilities.PreferenceManager;

public class AlarmReceiver extends BroadcastReceiver {

    String name;
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.hasExtra("UserName"))
        {
            name = intent.getStringExtra("UserName");
        }
        else
        {
            name = "";
        }
        String content = "You Scheduled a Call With "+ name;
        if(intent.hasExtra("Reminder"))
        {
            content = intent.getStringExtra("Reminder");
        }
        Intent intent1 = new Intent(context,Object.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Vidify")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Call Reminder")
                .setContentText(content)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());
    }
}
