package com.example.patientappointmentscheduler_usingfirebase.fragments;


import static android.widget.Toast.makeText;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.example.patientappointmentscheduler_usingfirebase.Interfaces.CloseModal;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class updateReservationFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String CATEGORY_NAME = "CATEGORY NAME";
    public static final String DOCTORS_NAME = "DOCTORS NAME";
    private static final String APPOINTMENT_DATE = "APPOINTMENT DATE";
    private static final String APPOINTMENT_TIME = "APPOINTMENT TIME";
    public static final String RESERVATION_ID = "RESERVATION ID";

    private String categoryName, doctorsName , scheduleDate, scheduleTime, reservationID;

    private TextView tvUpdateDoctorsName;

    private View view;
    private Spinner spAppointmentCategory, spDoctors;
    private ImageView mCloseButton;
    private Button mUpdateCancelButton, mUpdateReservationButton;

    //date picker for visit
    private DatePickerDialog datePickerDialog;
    private Button dateOfAppointment;

    //time picker
    private Button timeOfAppointment;
    private int hour, minute;

    //interface
    private CloseModal closeModal;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //fragment constructor
    public updateReservationFragment(CloseModal closeModal){
        this.closeModal = closeModal;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeModal.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_update_reservation, container, false);

        spAppointmentCategory = view.findViewById(R.id.spUpdateCategory);
        spAppointmentCategory.setOnItemSelectedListener(this);

        spDoctors = view.findViewById(R.id.spUpdateDoctorsName);
        tvUpdateDoctorsName = view.findViewById(R.id.tvUpdateDoctorsName);

        //date picker
        dateOfAppointment = view.findViewById(R.id.btnUpdateAppointmentDate);
        selectDate();
        initDatePicker();
        //time picker
        timeOfAppointment = view.findViewById(R.id.btnUpdateAppointmentTime);
        selectTime();

        mCloseButton = view.findViewById(R.id.ivCloseButton);
        mUpdateCancelButton = view.findViewById(R.id.btnUpdateCancel);
        mUpdateReservationButton = view.findViewById(R.id.btnUpdateSave);

        getBundles();
        getUpdateAppointmentCategory();
        getUpdateDoctor();
        closeFragment();
        cancelFragment();
        submitUpdateReservation();
        return view;
    }

    private void submitUpdateReservation() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Reservations");

        mUpdateReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReservation();
            }
        });
    }

    private void updateReservation() {

        String newCategory = spAppointmentCategory.getSelectedItem().toString();
        String newDoctor = spDoctors.getSelectedItem().toString();
        String newDate = dateOfAppointment.getText().toString();
        String newTime = timeOfAppointment.getText().toString();
        String newDateTime = (newDate + " " + newTime);

        HashMap hashMap = new HashMap();
        hashMap.put("appointmentCategory", newCategory);
        hashMap.put("doctorsName", newDoctor);
        hashMap.put("appointmentDateTime", newDateTime);

        databaseReference.child(reservationID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                //TODO add dialog bar
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction().remove(updateReservationFragment.this).commit();
            }
        });

    }

    private void getBundles() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            categoryName = bundle.getString(CATEGORY_NAME);
            doctorsName = bundle.getString(DOCTORS_NAME);
            scheduleDate = bundle.getString(APPOINTMENT_DATE);
            scheduleTime = bundle.getString(APPOINTMENT_TIME);
            reservationID = bundle.getString(RESERVATION_ID);

            dateOfAppointment.setText(scheduleDate);
            timeOfAppointment.setText(scheduleTime);
        }
    }

    private void selectTime() {
        timeOfAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), onTimeSetListener, hour, minute, true);
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

    private String makeDateString(int day, int month, int year){
        return month + "-" + day + "-" + year;
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

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    private void cancelFragment() {
        mUpdateCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(updateReservationFragment.this).commit();
            }
        });
    }

    private void closeFragment() {
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(updateReservationFragment.this).commit();
            }
        });
    }

    private void getUpdateAppointmentCategory() {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.child("AppointmentCategory").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> categories = new ArrayList<String>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String categoryName = snapshot.child("categoryName").getValue(String.class);
                    categories.add(categoryName);
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.color_spinner_layout, categories);
                categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
                spAppointmentCategory.setAdapter(categoryAdapter);

                //compare category name to position in spinner
                if (categoryName != null) {
                    int categorySpinnerPosition = categoryAdapter.getPosition(categoryName);
                    spAppointmentCategory.setSelection(categorySpinnerPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void getUpdateDoctor() {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.child("Doctors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> doctors = new ArrayList<String>();

                for (DataSnapshot getDataSnapshot: dataSnapshot.getChildren()) {
                    String fullName = getDataSnapshot.child("fullName").getValue(String.class);
                    doctors.add(fullName);
                }

                ArrayAdapter<String> docAdapter = new ArrayAdapter<String>(getActivity(),
                        R.layout.color_spinner_layout, doctors);
                docAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
                spDoctors.setAdapter(docAdapter);

                //compare doctors name to position in spinner
                if (doctorsName != null) {
                    int doctorSpinnerPosition = docAdapter.getPosition(doctorsName);
                    spDoctors.setSelection(doctorSpinnerPosition);
                } else if (doctorsName.equals("N/A")){
                    spDoctors.setSelection(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void hideDoctorIfLabTest() {
        String getCategory = spAppointmentCategory.getSelectedItem().toString();

        if(getCategory.equals("PRIMARY CARE CONSULTATION") || getCategory.equals("ANNUAL CHECK UP / APE")) {
            spDoctors.setVisibility(View.VISIBLE);
            tvUpdateDoctorsName.setVisibility(View.VISIBLE);
            getUpdateDoctor();
        } else {
            spDoctors.setVisibility(View.GONE);
            tvUpdateDoctorsName.setVisibility(View.GONE);
            getUpdateDoctor();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        hideDoctorIfLabTest();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        return;
    }
}