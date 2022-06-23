package com.example.patientappointmentscheduler_usingfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.databinding.ActivityAlarmBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    private ActivityAlarmBinding binding;
    private MaterialTimePicker materialTimePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker timePicker123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        calendar = Calendar.getInstance();
        /*calendar.set(Calendar.YEAR, 2022);
        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.DAY_OF_MONTH, 2);*/
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 20);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        binding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        binding.btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        binding.btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });

        binding.btnCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
            }
        });
    }

    private void cancelAlarm() {
        Intent intent = new Intent (this, AlarmReceiver.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast
                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getBroadcast
                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        if (alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled!", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent (this, AlarmReceiver.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast
                        (this, 0, intent, PendingIntent.FLAG_MUTABLE);
            }
            else
            {
                pendingIntent = PendingIntent.getBroadcast
                        (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Notification set"+calendar, Toast.LENGTH_SHORT).show();
            Log.v("CHECK", String.valueOf(calendar));
    }

    private void showTimePicker() {
        materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(9)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        materialTimePicker.show(getSupportFragmentManager(), "louchin");

        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (materialTimePicker.getHour() > 12){
                    binding.selectedTime.setText(String.format("%02d:%02d PM", materialTimePicker.getHour()-12, materialTimePicker.getMinute()));
                } else {
                    binding.selectedTime.setText(String.format("%02d:%02d AM", materialTimePicker.getHour(), materialTimePicker.getMinute()));
                }*/

                binding.selectedTime.setText(String.format("%02d:%02d AM", materialTimePicker.getHour(), materialTimePicker.getMinute()));

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());
                calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
        });
    }

    private void createNotificationChannel() {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S){
            CharSequence name = "foxReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notificationID", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}