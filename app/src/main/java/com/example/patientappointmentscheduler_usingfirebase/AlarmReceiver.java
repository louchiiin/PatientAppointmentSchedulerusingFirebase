package com.example.patientappointmentscheduler_usingfirebase;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notificationClicked = new Intent(context, PatientReservationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent pendingIntent = PendingIntent.getActivity
                    (context, 0, notificationClicked, PendingIntent.FLAG_MUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notificationID")
                    .setSmallIcon(R.drawable.bell_icon)
                    .setContentTitle("MyClinic PH")
                    .setContentText("You're reservation is underway!")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123, builder.build());
            Log.v("AlarmReceiver:", "Android12");
        }
        else
        {
            PendingIntent pendingIntent = PendingIntent.getActivity
                    (context, 0, notificationClicked, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notificationID")
                    .setSmallIcon(R.drawable.bell_icon)
                    .setContentTitle("MyClinic PH")
                    .setContentText("You're reservation is underway!")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(123, builder.build());
            Log.v("AlarmReceiver:", "NonAndroid12");
        }

        /*PendingIntent pendingIntent = PendingIntent.getActivity
                (context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notificationID")
                .setSmallIcon(R.drawable.bell_icon)
                .setContentTitle("MyClinic PH")
                .setContentText("You're reservation is underway!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());*/
    }

}
