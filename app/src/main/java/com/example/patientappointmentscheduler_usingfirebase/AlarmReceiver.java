package com.example.patientappointmentscheduler_usingfirebase;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AlarmReceiver extends BroadcastReceiver {

    private FirebaseAuth mAuth;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    String getCategoryName = intent.getStringExtra("CATEGORY");
        Intent notificationClicked = new Intent(context, PatientReservationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent pendingIntent = PendingIntent.getActivity
                    (context, 0, notificationClicked, PendingIntent.FLAG_MUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notificationID")
                    .setSmallIcon(R.drawable.bell_icon)
                    .setContentTitle("MyClinic PH")
                    .setContentText("You're reservation is underway for " + getCategoryName + "!")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123, builder.build());
            Log.v("AlarmReceiver:", "Android12");
        } else {
            PendingIntent pendingIntent = PendingIntent.getActivity
                    (context, 0, notificationClicked, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notificationID")
                    .setSmallIcon(R.drawable.bell_icon)
                    .setContentTitle("MyClinic PH")
                    .setContentText("You're reservation is underway for " + getCategoryName + "!")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123, builder.build());
            Log.v("AlarmReceiver:", "NonAndroid12");
        }
    }
}
