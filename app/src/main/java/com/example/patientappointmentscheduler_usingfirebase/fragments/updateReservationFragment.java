package com.example.patientappointmentscheduler_usingfirebase.fragments;


import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.patientappointmentscheduler_usingfirebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class updateReservationFragment extends Fragment {
    private View view;
    private Spinner spAppointmentCategory, spDoctors;
    private Button mCloseButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_update_reservation, container, false);

        spAppointmentCategory = view.findViewById(R.id.spUpdateCategory);
        mCloseButton = view.findViewById(R.id.btnCloseFragment);
        Fragment fragment = this;
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        getUpdateAppointmentCategory();
        return view;
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }
}