package com.example.patientappointmentscheduler_usingfirebase;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.Interfaces.CloseModal;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.bottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.topNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.updateReservationFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservationInfoActivity extends AppCompatActivity implements CloseModal {

    public static final String CATEGORY_NAME = "CATEGORY NAME";
    public static final String DOCTORS_NAME = "DOCTORS NAME";
    public static final String PATIENTS_NAME = "PATIENTS NAME";
    public static final String SCHEDULE_DATETIME = "SCHEDULE DATETIME";
    public static final String CREATED_DATE = "CREATED DATE";
    public static final String RESERVATION_ID = "RESERVATION ID";
    public static final String ERROR = "ERROR";
    private static final String CALENDAR_TITLE = "MyClinicPH Appointment";
    private static final String CALENDAR_LOCATION = "BLDG 1234, CEBU CITY";
    private static final String APPOINTMENT_DATE = "APPOINTMENT DATE";
    private static final String APPOINTMENT_TIME = "APPOINTMENT TIME";

    private TextView tvGetCategoryName, tvGetDoctorsName, tvGetPatientsName, tvGetScheduleDateTime, tvGetCreatedDate, tvGetReservationID, mPushNotificationButton;

    private Button mCancelReservationButton, mAddToGoogleCalendarButton, mUpdateReservationButton;

    private ProgressDialog dialog, updateDialog;

    private DatabaseReference databaseReference;

    //for notification
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_info);

        //dialog before displaying values
        dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mAddToGoogleCalendarButton = findViewById(R.id.btnAddToGoogleCalendar);
        mCancelReservationButton = findViewById(R.id.btnCancelReservation);
        mUpdateReservationButton = findViewById(R.id.btnUpdateReservation);
        mPushNotificationButton = findViewById(R.id.tvPushNotification);

        getIntentValues();
        addToGoogleCalendar();
        cancelAppointment();
        updateReservationModal();
        addPushNotification();
        displayTopNavBar(new topNavBarFragment("Appointment Information"));
        displayBottomNavBar(new bottomAppNavBarFragment());
    }

    private void addPushNotification() {
        mPushNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPushNotificationDialog().show();
            }
        });
    }

    private Dialog addPushNotificationDialog() {

        String getScheduleDateTime = getIntent().getStringExtra(SCHEDULE_DATETIME);

        //get getScheduleDateTime String to DateFormat
        DateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
        //format each values from getScheduleDateTime
        DateFormat outputMonth = new SimpleDateFormat("MM", Locale.getDefault());
        DateFormat outputDay = new SimpleDateFormat("dd", Locale.getDefault());
        DateFormat outputYear = new SimpleDateFormat("yyyy", Locale.getDefault());
        DateFormat outputHour = new SimpleDateFormat("H", Locale.getDefault()); //to not display leading zeroes
        DateFormat outputMinutes = new SimpleDateFormat("mm", Locale.getDefault());

        Date date = null;
        try {
            date = inputFormat.parse(getScheduleDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //cast as String for each values from getScheduleDateTime
        String getMonth = outputMonth.format(date);
        String getDay = outputDay.format(date);
        String getYear = outputYear.format(date);
        String getHour = outputHour.format(date);
        String getMinutes = outputMinutes.format(date);

        //get Calendar instance
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(getYear));
        calendar.set(Calendar.MONTH, Integer.parseInt(getMonth)-1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(getDay));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(getHour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(getMinutes));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        /*calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 32);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);*/

        patientReservationNotificationChannel();

        AlertDialog.Builder builder = new AlertDialog.Builder(ReservationInfoActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Notification");
        builder.setMessage("Set push notification for this appointment?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @SuppressLint("UnspecifiedImmutableFlag")
            public void onClick(DialogInterface dialog, int which) {
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent intent = new Intent (ReservationInfoActivity.this, AlarmReceiver.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getBroadcast
                            (ReservationInfoActivity.this, 0, intent, PendingIntent.FLAG_MUTABLE);
                    Log.v("NOTIF", "SetSuccessNotification");
                }
                else
                {
                    pendingIntent = PendingIntent.getBroadcast
                            (ReservationInfoActivity.this, 0, intent, 0);

                    Log.v("NOTIF", "SetSuccessNotificationNonAndroid12");
                }

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(ReservationInfoActivity.this, "Notification set "+calendar, Toast.LENGTH_SHORT).show();
                Log.v("CHECK", String.valueOf(calendar));

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void patientReservationNotificationChannel() {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S){
            CharSequence name = "Louchin Channel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notificationID", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        } else {
            CharSequence name = "Louchin Channel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notificationID", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void close() {
        mAddToGoogleCalendarButton.setEnabled(true);
        mCancelReservationButton.setEnabled(true);
        mUpdateReservationButton.setEnabled(true);
    }

    private void updateReservationModal() {
        CloseModal tempActivity = this;
        mUpdateReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog = new ProgressDialog(ReservationInfoActivity.this);
                updateDialog.setTitle(R.string.loading_dialog);
                updateDialog.show();
                updateDialog.setCanceledOnTouchOutside(false);
                updateDialog.setCancelable(false);

                mAddToGoogleCalendarButton.setEnabled(false);
                mCancelReservationButton.setEnabled(false);
                mUpdateReservationButton.setEnabled(false);

                String getCategoryName = getIntent().getStringExtra(CATEGORY_NAME);
                String getDoctorsName = getIntent().getStringExtra(DOCTORS_NAME);
                String getScheduleDateTime = getIntent().getStringExtra(SCHEDULE_DATETIME);
                String getReservationID = getIntent().getStringExtra(RESERVATION_ID);
                //get getScheduleDateTime String to DateFormat
                DateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
                //format each values from getScheduleDateTime
                DateFormat outputDate = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                DateFormat outputTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

                Date date = null;
                try {
                    date = inputFormat.parse(getScheduleDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //cast as String for each values from getScheduleDateTime
                String getDate = outputDate.format(date);
                String getTime = outputTime.format(date);

                Bundle bundle = new Bundle();
                bundle.putString(CATEGORY_NAME, getCategoryName);
                bundle.putString(DOCTORS_NAME, getDoctorsName);
                bundle.putString(APPOINTMENT_DATE, getDate);
                bundle.putString(APPOINTMENT_TIME, getTime);
                bundle.putString(RESERVATION_ID, getReservationID);
                updateReservationFragment passBundle = new updateReservationFragment(tempActivity);
                passBundle.setArguments(bundle);
                displayUpdateReservation(passBundle);
                updateDialog.dismiss();
            }
        });
    }

    private void displayUpdateReservation(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutUpdateReservation, fragment);
        fragmentTransaction.commit();
    }

    private void displayTopNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.patientReservationInfoTopNav, fragment);
        fragmentTransaction.commit();
    }

    private void displayBottomNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutReservationInfoBottomAppNavBar, fragment);
        fragmentTransaction.commit();
    }

    private void getIntentValues() {
        //initialize
        tvGetCategoryName = findViewById(R.id.tvGetCategoryName);
        tvGetDoctorsName = findViewById(R.id.tvGetDoctorsName);
        tvGetPatientsName = findViewById(R.id.tvGetPatientsName);
        tvGetScheduleDateTime = findViewById(R.id.tvGetScheduleDateTime);
        tvGetCreatedDate = findViewById(R.id.tvGetCreatedDate);
        tvGetReservationID = findViewById(R.id.tvGetReservationID);
        //to string
        String getCategoryName = getIntent().getStringExtra(CATEGORY_NAME);
        String getDoctorsName = getIntent().getStringExtra(DOCTORS_NAME);
        String getPatientsName = getIntent().getStringExtra(PATIENTS_NAME);
        String getScheduleDateTime = getIntent().getStringExtra(SCHEDULE_DATETIME);
        String getCreatedDate = getIntent().getStringExtra(CREATED_DATE);
        String getReservationID = getIntent().getStringExtra(RESERVATION_ID);

        tvGetCategoryName.setText(getCategoryName);
        tvGetDoctorsName.setText(getDoctorsName);
        tvGetPatientsName.setText(getPatientsName);
        tvGetScheduleDateTime.setText(getScheduleDateTime);
        tvGetCreatedDate.setText(getCreatedDate);
        tvGetReservationID.setText(getReservationID);

        dialog.dismiss();
    }

    private void addToGoogleCalendar() {
        mAddToGoogleCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToGoogleCalendarDialog().show();
            }
        });

    }

    private void cancelAppointment() {
        mCancelReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAppointmentDialog().show();
            }
        });
    }

    private Dialog addToGoogleCalendarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReservationInfoActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to add this appointment to Google Calendar?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int which) {
                String getCurrentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String getCategoryName = getIntent().getStringExtra(CATEGORY_NAME);
                String getDoctorsName = getIntent().getStringExtra(DOCTORS_NAME);
                String getPatientsName = getIntent().getStringExtra(PATIENTS_NAME);
                String getScheduleDateTime = getIntent().getStringExtra(SCHEDULE_DATETIME);

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE, CALENDAR_TITLE);
                intent.putExtra(CalendarContract.Events.DESCRIPTION,
                        "Good day! \n" + getPatientsName + "," + "\n\nPlease be there on the clinic before your scheduled appointment.\n\nAppointment Details\n"
                                + getCategoryName + "\n" + getDoctorsName + "\n" + getPatientsName + "\n" + getScheduleDateTime +"\n\nThank you, \nmyClinicPH Team");
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, CALENDAR_LOCATION);
                intent.putExtra(CalendarContract.Events.ALL_DAY, false);

                //get getScheduleDateTime String to DateFormat
                DateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
                //format each values from getScheduleDateTime
                DateFormat outputMonth = new SimpleDateFormat("MM", Locale.getDefault());
                DateFormat outputDay = new SimpleDateFormat("dd", Locale.getDefault());
                DateFormat outputYear = new SimpleDateFormat("yyyy", Locale.getDefault());
                DateFormat outputHour = new SimpleDateFormat("HH", Locale.getDefault());
                DateFormat outputMinutes = new SimpleDateFormat("mm", Locale.getDefault());

                Date date = null;
                try {
                    date = inputFormat.parse(getScheduleDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //cast as String for each values from getScheduleDateTime
                String getMonth = outputMonth.format(date);
                String getDay = outputDay.format(date);
                String getYear = outputYear.format(date);
                String getHour = outputHour.format(date);
                String getMinutes = outputMinutes.format(date);

                //convert String to int all values from getScheduleDateTime
                Calendar startTime = Calendar.getInstance();
                startTime.set(Integer.parseInt(getYear), Integer.parseInt(getMonth)-1, Integer.parseInt(getDay), Integer.parseInt(getHour), Integer.parseInt(getMinutes));
                Calendar endTime = Calendar.getInstance();
                endTime.set(Integer.parseInt(getYear), Integer.parseInt(getMonth)-1, Integer.parseInt(getDay), Integer.parseInt(getHour)+1, Integer.parseInt(getMinutes));

                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
                intent.putExtra(Intent.EXTRA_EMAIL, getCurrentUserEmail);

                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }else{
                    Toast.makeText(ReservationInfoActivity.this,
                            "Google Calendar is not installed in your device", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private Dialog cancelAppointmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReservationInfoActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to cancel this appointment?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String getReservationID = getIntent().getStringExtra(RESERVATION_ID);
                databaseReference = FirebaseDatabase.getInstance().getReference("Reservations");
                Query loginQuery = databaseReference.orderByKey().equalTo(getReservationID);

                loginQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot removeFromDB: dataSnapshot.getChildren()) {
                            removeReservationDialog().show();
                            removeFromDB.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(ERROR, "onCancelled", databaseError.toException());
                    }
                });
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private Dialog removeReservationDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Reservation successfully deleted")
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ReservationInfoActivity.this, PatientReservationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        return builder.create();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentById(R.id.frameLayoutUpdateReservation);
        if(fragment!=null){
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }else{
            super.onBackPressed();
            finish();
        }
    }

}