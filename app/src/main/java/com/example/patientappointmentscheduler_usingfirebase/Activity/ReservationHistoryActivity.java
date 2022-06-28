package com.example.patientappointmentscheduler_usingfirebase.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.patientappointmentscheduler_usingfirebase.Adapter.ReservationHistoryAdapter;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.BottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.TopNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.model.ReservationList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReservationHistoryActivity extends AppCompatActivity {
    private ReservationHistoryAdapter reservationHistoryAdapter;
    private DatabaseReference databaseReference;

    private TextView mHistoryResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_history);
        animateLoading();
        mHistoryResults = findViewById(R.id.total_reservation_history);

        displayReservationHistoryList();
        displayTopNavBar(new TopNavBarFragment("Reservations History", this));
        displayBottomNavBar(new BottomAppNavBarFragment(this));
    }

    private void displayReservationHistoryList() {
        //initialize
        RecyclerView rvReservations = findViewById(R.id.reservation_history_list);
        reservationHistoryAdapter = new ReservationHistoryAdapter(this, ReservationHistoryActivity.this);
        rvReservations.setAdapter(reservationHistoryAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getValuesFromFirebase();
    }

    private void animateLoading() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void displayTopNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reservation_history_top_nav, fragment);
        fragmentTransaction.commit();
    }

    private void displayBottomNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reservation_history_bottom_nav, fragment);
        fragmentTransaction.commit();
    }

    private void getValuesFromFirebase() {
        String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child("Reservations").orderByChild("loggedInUid_status").equalTo(currentFirebaseUser+ "_done")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ReservationList> listReservationHistory = new ArrayList<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Log.d("User key", dataSnapshot.getKey());
                            Log.d("User ref", dataSnapshot.getRef().toString());
                            Log.d("User val", dataSnapshot.getValue().toString());
                            //get unique key for each reservation
                            String uniqueKey = dataSnapshot.getKey();
                            ReservationList reservationList = dataSnapshot.getValue(ReservationList.class);
                            reservationList.setReservationID(uniqueKey);
                            listReservationHistory.add(reservationList);
                        }
                        reservationHistoryAdapter.submitList(listReservationHistory);
                        Collections.sort(listReservationHistory);

                        mHistoryResults.setText(" (" + listReservationHistory.size() + ")");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ERROR", "error: " + error);
                    }

                });
    }

    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}