package com.example.patientappointmentscheduler_usingfirebase;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    //ReservationAdapter reservationAdapter;
    ReservationListAdapter reservationListAdapter;
    DatabaseReference databaseReference;
    //ArrayList<ReservationList> list;
    private ProgressDialog dialog;

    private TextView tvNoResultsFound, mNumberOfItems;
    private Button btnBackToHome;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_reservation);
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
        tvNoResultsFound = findViewById(R.id.tvNoResultsFound);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientReservationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        displayReservationList();
        displayTopNavBar(new TopNavBarFragment("Reservations"));
        displayBottomNavBar(new BottomAppNavBarFragment());
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
        reservationListAdapter = new ReservationListAdapter();
        rvReservations.setAdapter(reservationListAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Reservations");
        /*rvReservations.setHasFixedSize(true);
        rvReservations.setLayoutManager(new LinearLayoutManager(this));*/

        //list = new ArrayList<>();
        /*reservationAdapter = new ReservationAdapter(this, list);
        rvReservations.setAdapter(reservationAdapter);*/
        getValuesFromFirebase();
    }

    private void getValuesFromFirebase() {
        String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.orderByChild("loggedInUid").equalTo(currentFirebaseUser)
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
                            //list.add(reservationList);
                            //index++;
                        }
                        reservationListAdapter.submitList(listReservation);
                        //reservationAdapter.notifyDataSetChanged();
                        //rvReservations.invalidate();
                        dialog.dismiss();
                        //mNumberOfItems.setText(" (" + list.size() + ")");
                        mNumberOfItems.setText(" (" + listReservation.size() + ")");

                        //if (list.size() == 0) {
                        if (listReservation.size() == 0) {
                            tvNoResultsFound.setVisibility(View.VISIBLE);
                            btnBackToHome.setVisibility(View.VISIBLE);
                        } else {
                            tvNoResultsFound.setVisibility(View.GONE);
                            btnBackToHome.setVisibility(View.GONE);
                        }
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