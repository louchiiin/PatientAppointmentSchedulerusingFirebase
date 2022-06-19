package com.example.patientappointmentscheduler_usingfirebase;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientappointmentscheduler_usingfirebase.Adapter.ReservationAdapter;
import com.example.patientappointmentscheduler_usingfirebase.Adapter.ReservationListAdapter;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.bottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.topNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.model.ReservationList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientReservationActivity extends AppCompatActivity {
    RecyclerView rvReservations;
    //ReservationListAdapter reservationListAdapter;
    ReservationAdapter reservationAdapter;
    DatabaseReference databaseReference;
    ArrayList<ReservationList> list;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_reservation);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading..");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        displayReservationList();
        displayTopNavBar(new topNavBarFragment("Reservations"));
        displayBottomNavBar(new bottomAppNavBarFragment());
    }

    private void displayTopNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.patientReservationTopNav, fragment);
        fragmentTransaction.commit();
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
        /*reservationListAdapter = new ReservationListAdapter();
        rvReservations.setAdapter(reservationListAdapter);*/

        databaseReference = FirebaseDatabase.getInstance().getReference("Reservations");

        rvReservations.setHasFixedSize(true);
        rvReservations.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        reservationAdapter = new ReservationAdapter(this, list);
        rvReservations.setAdapter(reservationAdapter);

        String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.orderByChild("loggedInUid").equalTo(currentFirebaseUser)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //int index = 0;
                //List<ReservationList> listReservation = new ArrayList<>(

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.d("User key", dataSnapshot.getKey());
                    Log.d("User ref", dataSnapshot.getRef().toString());
                    Log.d("User val", dataSnapshot.getValue().toString());
                    //get unique key for each reservation
                    String uniqueKey = dataSnapshot.getKey();
                    ReservationList reservationList = dataSnapshot.getValue(ReservationList.class);
                    reservationList.setReservationID(uniqueKey);
                    //reservationList.setReservationID(String.valueOf(index));
                    list.add(reservationList);
                    //index++;
                }
                //reservationListAdapter.submitList(listReservation);
                reservationAdapter.notifyDataSetChanged();
                dialog.dismiss();

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

    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}