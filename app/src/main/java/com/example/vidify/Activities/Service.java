package com.example.vidify.Activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.vidify.Utilities.PreferenceManager;

import java.util.Calendar;

public class Service extends android.app.Service {
    PreferenceManager preferenceManager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        preferenceManager = new PreferenceManager(getApplicationContext());
            long prevTime = preferenceManager.getLong("time");
            prevTime = prevTime - 300000;
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent1 = new Intent(this,AlarmReceiver.class);

           PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP,prevTime,pendingIntent);

            Toast.makeText(getApplicationContext(), "Service Running", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;

    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "VidifyReminderChannel";
            String description = "Alarm Manager Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Vidify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
