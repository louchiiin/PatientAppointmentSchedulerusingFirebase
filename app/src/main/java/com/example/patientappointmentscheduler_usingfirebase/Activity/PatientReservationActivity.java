package com.example.patientappointmentscheduler_usingfirebase.Activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.patientappointmentscheduler_usingfirebase.Adapter.ReservationListAdapter;
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

public class PatientReservationActivity extends AppCompatActivity {
    private ReservationListAdapter reservationListAdapter;
    private DatabaseReference databaseReference;
    private ProgressDialog dialog;

    private TextView mNoResults;
    private TextView mNumberOfItems;
    private TextView mHistory;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_reservation);
        animateLoading();
        dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_pull_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                getValuesFromFirebase();
            }
        });

        mNumberOfItems = findViewById(R.id.number_of_items);
        mNoResults = findViewById(R.id.no_results_found);
        mHistory = findViewById(R.id.reservation_history);

        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientReservationActivity.this, ReservationHistoryActivity.class);
                startActivity(intent);
            }
        });

        displayReservationList();
        displayTopNavBar(new TopNavBarFragment("Reservations", this));
        displayBottomNavBar(new BottomAppNavBarFragment(this));
    }

    private void animateLoading() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void displayTopNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reservation_top_nav, fragment);
        fragmentTransaction.commit();
    }

    private void displayBottomNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.reservation_bottom_nav_bar, fragment);
        fragmentTransaction.commit();
    }

    private void displayReservationList() {
        //initialize
        RecyclerView rvReservations = findViewById(R.id.reservation_lists);
        reservationListAdapter = new ReservationListAdapter(this, PatientReservationActivity.this);
        rvReservations.setAdapter(reservationListAdapter);
        //databaseReference = FirebaseDatabase.getInstance().getReference("Reservations");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getValuesFromFirebase();
    }

    private void getValuesFromFirebase() {
        String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //databaseReference.orderByChild("loggedInUid").equalTo(currentFirebaseUser)
        databaseReference.child("Reservations").orderByChild("loggedInUid_status").equalTo(currentFirebaseUser+ "_active")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ReservationList> listReservation = new ArrayList<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Log.d("User key", dataSnapshot.getKey());
                            Log.d("User ref", dataSnapshot.getRef().toString());
                            Log.d("User val", dataSnapshot.getValue().toString());
                            //get unique key for each reservation
                            String uniqueKey = dataSnapshot.getKey();
                            ReservationList reservationList = dataSnapshot.getValue(ReservationList.class);
                            reservationList.setReservationID(uniqueKey);
                            listReservation.add(reservationList);
                        }
                        reservationListAdapter.submitList(listReservation);
                        Collections.sort(listReservation);

                        dialog.dismiss();
                        mNumberOfItems.setText(" (" + listReservation.size() + ")");

                        if (listReservation.size() == 0) {
                            mNoResults.setVisibility(View.VISIBLE);
                        } else {
                            mNoResults.setVisibility(View.GONE);
                        }
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