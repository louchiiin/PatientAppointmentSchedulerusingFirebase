package com.example.patientappointmentscheduler_usingfirebase;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientappointmentscheduler_usingfirebase.Adapter.ReservationListAdapter;
import com.example.patientappointmentscheduler_usingfirebase.fragments.bottom_app_nav_fragment;
import com.example.patientappointmentscheduler_usingfirebase.model.ReservationList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientReservationActivity extends AppCompatActivity {
    RecyclerView rvReservations;
    ReservationListAdapter reservationListAdapter;
    DatabaseReference databaseReference;

    private TextView tvReservationBack;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_reservation);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading..");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
                displayReservationList();
            }
        }, 1500); //

        displayReservationList();
        backToMain();
        displayBottomNavBar(new bottom_app_nav_fragment());
    }

    private void displayBottomNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutReservationsBottomAppNavBar, fragment);
        fragmentTransaction.commit();
    }

    private void displayReservationList() {
        //initialize
        rvReservations = findViewById(R.id.rvReservations);
        reservationListAdapter = new ReservationListAdapter();
        rvReservations.setAdapter(reservationListAdapter);

        String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Reservations");

        databaseReference.orderByChild("loggedInUid").equalTo(currentFirebaseUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int index = 0;
                List<ReservationList> listReservation = new ArrayList<>();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.d("User key", dataSnapshot.getKey());
                    Log.d("User ref", dataSnapshot.getRef().toString());
                    Log.d("User val", dataSnapshot.getValue().toString());
                    //get unique key for each reservation
                    String uniqueKey = dataSnapshot.getKey();
                    ReservationList reservationList = dataSnapshot.getValue(ReservationList.class);
                    reservationList.setReservationID(uniqueKey);
                    //reservationList.setReservationID(String.valueOf(index));
                    listReservation.add(reservationList);
                    //index++;
                }
                reservationListAdapter.submitList(listReservation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //mock data for testing
        /*listReservation.add(new ReservationList("121", "123", "APE",
                "Doctor1", "2022-06-19", "16:00", "2022-05-24", "RESERVED"));
        reservationListAdapter.submitList(listReservation);*/
    }

    private void backToMain() {
        tvReservationBack = findViewById(R.id.tvReservationBack);
        tvReservationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent backToMain = new Intent(PatientReservationActivity.this, MainActivity.class);
                startActivity(backToMain);*/
                finish();
            }
        });
    }

    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PatientReservationActivity.this, MainActivity.class);
        startActivity(intent);
    }
}