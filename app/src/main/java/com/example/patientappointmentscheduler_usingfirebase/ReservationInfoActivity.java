package com.example.patientappointmentscheduler_usingfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class ReservationInfoActivity extends AppCompatActivity {
    public static final String CATEGORY_NAME = "CATEGORY NAME";
    public static final String DOCTORS_NAME = "DOCTORS NAME";
    public static final String PATIENTS_NAME = "PATIENTS NAME";
    public static final String SCHEDULE_DATE = "SCHEDULE DATE";
    public static final String SCHEDULE_TIME = "SCHEDULE TIME";
    public static final String CREATED_DATE = "CREATED DATE";

    private TextView tvReservationInfoBack, tvGetCategoryName, tvGetDoctorsName, tvGetPatientsName, tvGetScheduleDate, tvGetScheduleTime, tvGetCreatedDate;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_info);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading..");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
                getIntentValues();
            }
        }, 1500); //

        backButton();
    }

    private void getIntentValues() {
        //initialize
        tvGetCategoryName = findViewById(R.id.tvGetCategoryName);
        tvGetDoctorsName = findViewById(R.id.tvGetDoctorsName);
        tvGetPatientsName = findViewById(R.id.tvGetPatientsName);
        tvGetScheduleDate = findViewById(R.id.tvGetScheduleDate);
        tvGetScheduleTime = findViewById(R.id.tvGetScheduleTime);
        tvGetCreatedDate = findViewById(R.id.tvGetCreatedDate);
        //to string
        String getCategoryName = getIntent().getStringExtra(CATEGORY_NAME);
        String getDoctorsName = getIntent().getStringExtra(DOCTORS_NAME);
        String getPatientsName = getIntent().getStringExtra(PATIENTS_NAME);
        String getScheduleDate = getIntent().getStringExtra(SCHEDULE_DATE);
        String getScheduleTime = getIntent().getStringExtra(SCHEDULE_TIME);
        String getCreatedDate = getIntent().getStringExtra(CREATED_DATE);

        tvGetCategoryName.setText(getCategoryName);
        tvGetDoctorsName.setText(getDoctorsName);
        tvGetPatientsName.setText(getPatientsName);
        tvGetScheduleDate.setText(getScheduleDate);
        tvGetScheduleTime.setText(getScheduleTime);
        tvGetCreatedDate.setText(getCreatedDate);
    }

    private void backButton() {
        tvReservationInfoBack = findViewById(R.id.tvReservationInfoBack);
        tvReservationInfoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}