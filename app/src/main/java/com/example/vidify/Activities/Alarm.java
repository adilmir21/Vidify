package com.example.vidify.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vidify.R;
import com.example.vidify.Utilities.PreferenceManager;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.function.Predicate;

public class Alarm extends AppCompatActivity {

    MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    Button cancel;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        preferenceManager = new PreferenceManager(getApplicationContext());

        createNotificationChannel();
        showTimePicker();

        cancel = findViewById(R.id.cancelAlarm);
        cancel.setOnClickListener(v->
        {
            Intent intent = new Intent(this,AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
            if(alarmManager == null)
            {
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            }
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "Reminder Cancelled", Toast.LENGTH_SHORT).show();
            stopService(new Intent(getApplicationContext(),Service.class));
        });
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this,AlarmReceiver.class);
        intent.putExtra("UserName",getIntent().getStringExtra("UserName"));

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);

        Toast.makeText(getApplicationContext(), "Reminder Set Successfully", Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(getApplicationContext(),Service.class);
        intent1.putExtra("Time",calendar.getTimeInMillis());
        startService(new Intent(getApplicationContext(),Service.class));
        Intent intent2 = new Intent(getApplicationContext(),OtherUsers.class);
        startActivity(intent2);
    }

    private void showTimePicker() {
        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Set Reminder Time")
                .build();

        picker.show(getSupportFragmentManager(),"Vidify");
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                calendar.set(Calendar.MINUTE,picker.getMinute());
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                preferenceManager.putLong("time",calendar.getTimeInMillis());
                setAlarm();
            }
        });
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "VidifyReminderChannel";
            String description = "Alarm Manager Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Vidify",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}