package com.example.patientappointmentscheduler_usingfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.Fragments.BottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.TopNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.model.Reservations;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Button btnSubmit;
    private TextView tvDoctor, tvSchedulePatientsName;
    private Spinner spAppointmentCategory, spDoctors;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Reservations reservations;

    //date picker for visit
    private DatePickerDialog datePickerDialog;
    private Button dateOfAppointment;

    //time picker
    private Button timeOfAppointment;
    private int hour, minute;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        tvDoctor = findViewById(R.id.tvDoctor);
        spDoctors = findViewById(R.id.spDoctors);
        spAppointmentCategory = findViewById(R.id.spAppointmentCategory);
        spAppointmentCategory.setOnItemSelectedListener(this);

        //date picker for schedule of visit
        initDatePicker();
        dateOfAppointment = findViewById(R.id.btnSelectAppointmentDate);
        dateOfAppointment.setText(getTodayDate());
        selectDate();
        //time picker
        timeOfAppointment = findViewById(R.id.select_appointment_time);
        selectTime();
        //get logged in user and cast into a textview
        getPatientsName();
        getAppointmentCategory();
        getDoctor();
        dialog = new ProgressDialog(this);
        submitAppointment();
        displayTopNavBar(new TopNavBarFragment("Schedule an Appointment"));
        displayBottomNavBar(new BottomAppNavBarFragment());
    }

    private void displayTimePickerDialog(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.time_picker_frame, fragment);
        fragmentTransaction.commit();
    }

    private void displayTopNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.scheduleTopNav, fragment);
        fragmentTransaction.commit();
    }

    private void displayBottomNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutScheduleBottomAppNavBar, fragment);
        fragmentTransaction.commit();
    }

    private void getPatientsName() {
        String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidReference = rootReference.child("PatientInfo").child(currentFirebaseUser);
        ValueEventListener eventListener = new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child("firstName").getValue(String.class);
                String lastName = snapshot.child("lastName").getValue(String.class);
                tvSchedulePatientsName = findViewById(R.id.tvSchedulePatientsName);
                tvSchedulePatientsName.setText(firstName + " " + lastName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", error.getMessage());
            }
        }; uidReference.addListenerForSingleValueEvent(eventListener);
    }

    private void selectTime() {
        timeOfAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //displayTimePickerDialog(new TimePickerDialogFragment());
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        hour = selectedHour;
                        minute = selectedMinute;
                        timeOfAppointment.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, onTimeSetListener, hour, minute, true);
                timePickerDialog.setTitle("Select a time");
                timePickerDialog.show();
            }
        });

    }

    private void selectDate() {
        dateOfAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.setTitle("Select a date");
                datePickerDialog.show();
            }
        });
    }

    private String getTodayDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year){
        return (String.format(Locale.getDefault(),"%02d-%02d-%02d",month, day, year));
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateOfAppointment.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }


    private void hideDoctorIfLabTest() {
        String getCategory = spAppointmentCategory.getSelectedItem().toString();

        if (getCategory.equals("PRIMARY CARE CONSULTATION") || getCategory.equals("ANNUAL CHECK UP / APE")) {
            spDoctors.setVisibility(View.VISIBLE);
            tvDoctor.setVisibility(View.VISIBLE);
            getDoctor();
        } else {
            spDoctors.setVisibility(View.GONE);
            tvDoctor.setVisibility(View.GONE);
            //getSpinner values again
            getDoctor();
        }
    }

    private void submitAppointment() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Reservations");
        reservations = new Reservations();

        btnSubmit = findViewById(R.id.btnSubmitAppointment);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                addReservation();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addReservation() {
        try{
            //logged in uid
            FirebaseUser getLoggedInUser = FirebaseAuth.getInstance().getCurrentUser();
            String loggedInUid = getLoggedInUser.getUid();

            //get selected appointment category and doctors name
            String appointmentCategory = spAppointmentCategory.getSelectedItem().toString().trim();
            String doctorsName = spDoctors.getSelectedItem().toString();

            //get value of patient from textview
            String patientsName = tvSchedulePatientsName.getText().toString();

            //get current date
            LocalDateTime getCurrentDateTime = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
            String currentDate = getCurrentDateTime.format(format);

            //get set date/time of appointment
            String appointmentDate = dateOfAppointment.getText().toString();
            String appointmentTime = timeOfAppointment.getText().toString();
            String dateAndTime = (appointmentDate + " " + appointmentTime);

            String status = "RESERVED";

            String getCategory = spAppointmentCategory.getSelectedItem().toString();

            if (getCategory.equals("PRIMARY CARE CONSULTATION") || getCategory.equals("ANNUAL CHECK UP / APE")){
                if (appointmentCategory.isEmpty()) {
                    emptyCategoryDialog().show();
                } else if (doctorsName.isEmpty()) {
                    emptyDoctorDialog().show();
                } else if (appointmentTime.isEmpty()) {
                    emptyTimeDialog().show();
                } else if (appointmentCategory != null && doctorsName!= null) {
                    Reservations reservations = new Reservations(loggedInUid, appointmentCategory,
                            doctorsName, patientsName, dateAndTime, currentDate, status);
                    databaseReference.push().setValue(reservations);
                    addSuccessDialog().show();
                } else {
                    Toast.makeText(ScheduleActivity.this, "Error has occurred", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (appointmentCategory.isEmpty()) {
                    emptyCategoryDialog().show();
                } else if (appointmentTime.isEmpty()) {
                    emptyTimeDialog().show();
                } else if (appointmentCategory != null) {
                    String multipleDoctors = "N/A";
                    Reservations reservations = new Reservations(loggedInUid, appointmentCategory, multipleDoctors,
                            patientsName, dateAndTime, currentDate, status);
                    databaseReference.push().setValue(reservations);
                    //dialog before displaying values
                    dialog.setTitle(R.string.loading_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    addSuccessDialog().show();
                } else {
                    Toast.makeText(ScheduleActivity.this, "Error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e){
            Toast.makeText(ScheduleActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
        }
    }

    private Dialog emptyTimeDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Appointment Time is empty, please select a preferred time!")
                .setTitle("ALERT")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    private Dialog emptyDoctorDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Physician is empty, please select a physician!")
                .setTitle("ALERT")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    private Dialog emptyCategoryDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Appointment Category is empty, please select a category!")
                .setTitle("ALERT")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    private Dialog addSuccessDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Reservation added successfully!")
                .setTitle("Success")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface successDialog, int id) {
                        dialog.dismiss();
                        Intent intent = new Intent(ScheduleActivity.this, PatientReservationActivity.class);
                        startActivity(intent);
                        /*finish();*/
                    }
                });
        return builder.create();
    }

    private void getDoctor() {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.child("Doctors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> doctors = new ArrayList<String>();

                for (DataSnapshot getDataSnapshot: dataSnapshot.getChildren()) {
                    String fullName = getDataSnapshot.child("fullName").getValue(String.class);
                    doctors.add(fullName);
                }

                ArrayAdapter<String> docAdapter = new ArrayAdapter<String>(ScheduleActivity.this,
                        R.layout.color_spinner_layout, doctors);
                docAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
                spDoctors.setAdapter(docAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void getAppointmentCategory() {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.child("AppointmentCategory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> categories = new ArrayList<String>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String categoryName = snapshot.child("categoryName").getValue(String.class);
                    categories.add(categoryName);
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(ScheduleActivity.this,
                        R.layout.color_spinner_layout, categories);
                categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
                spAppointmentCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        hideDoctorIfLabTest();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        return;
    }

    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ScheduleActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}