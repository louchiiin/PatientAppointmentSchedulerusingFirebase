package com.example.patientappointmentscheduler_usingfirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.fragments.bottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.fragments.topNavBarFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationInfoActivity extends AppCompatActivity {
    public static final String CATEGORY_NAME = "CATEGORY NAME";
    public static final String DOCTORS_NAME = "DOCTORS NAME";
    public static final String PATIENTS_NAME = "PATIENTS NAME";
    public static final String SCHEDULE_DATETIME = "SCHEDULE DATETIME";
    public static final String CREATED_DATE = "CREATED DATE";
    public static final String RESERVATION_ID = "RESERVATION ID";
    public static final String ERROR = "ERROR";

    private static final String CALENDAR_TITLE = "MyClinicPH Appointment";
    private static final String CALENDAR_DESCRIPTION = "Please be there on the clinic before your scheduled appointment.\n\nThank you, \nmyClinicPH Team";
    private static final String CALENDAR_LOCATION = "BLDG 1234, CEBU CITY";

    private TextView tvReservationInfoBack, tvGetCategoryName, tvGetDoctorsName, tvGetPatientsName, tvGetScheduleDateTime, tvGetCreatedDate, tvGetReservationID;

    private Button btnCancelReservation, btnAddToGoogleCalendar;

    private ProgressDialog dialog;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_info);

        //dialog before displaying values
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading..");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        getIntentValues();
        addToGoogleCalendar();
        cancelAppointment();
        displayTopNavBar(new topNavBarFragment("Reservations Information"));
        displayBottomNavBar(new bottomAppNavBarFragment());
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
        btnAddToGoogleCalendar = findViewById(R.id.btnAddToGoogleCalendar);
        btnAddToGoogleCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToGoogleCalendarDialog().show();
            }
        });

    }

    private void cancelAppointment() {
        btnCancelReservation = findViewById(R.id.btnCancelReservation);
        btnCancelReservation.setOnClickListener(new View.OnClickListener() {
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
            public void onClick(DialogInterface dialog, int which) {
                String getCurrentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String getCategoryName = getIntent().getStringExtra(CATEGORY_NAME);
                String getDoctorsName = getIntent().getStringExtra(DOCTORS_NAME);
                String getPatientsName = getIntent().getStringExtra(PATIENTS_NAME);
                String getScheduleDateTime = getIntent().getStringExtra(SCHEDULE_DATETIME);

                String startTime = "2022-02-1 09:00:00";
                String endTime = "2022-02-1 12:00:00";

                // Parsing the date and time
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date mStartTime = null;
                Date mEndTime = null;

                try {
                    mStartTime = mSimpleDateFormat.parse(startTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    mEndTime = mSimpleDateFormat.parse(endTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE, CALENDAR_TITLE);
                intent.putExtra(CalendarContract.Events.DESCRIPTION,
                        "Good day! \n" + getPatientsName + "," + "\n\nPlease be there on the clinic before your scheduled appointment.\n\nAppointment Details\n"
                                + getCategoryName + "\n" + getDoctorsName + "\n" + getPatientsName + "\n" + getScheduleDateTime +"\n\nThank you, \nmyClinicPH Team");
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, CALENDAR_LOCATION);
                intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, mStartTime);
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, mEndTime);
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

    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}