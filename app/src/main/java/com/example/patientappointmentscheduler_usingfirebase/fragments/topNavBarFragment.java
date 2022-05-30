package com.example.patientappointmentscheduler_usingfirebase.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.MainActivity;
import com.example.patientappointmentscheduler_usingfirebase.PersonalInformationActivity;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.ScheduleActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link topNavBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class topNavBarFragment extends Fragment {
    private View view;
    private TextView tvBack, tvTopNavTitle;
    private String navBarTitle;


    public topNavBarFragment(String navTitle) {
        // Required empty public constructor
        this.navBarTitle = navTitle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_top_nav_bar, container, false);

        tvBack = view.findViewById(R.id.tvBack);
        tvTopNavTitle = view.findViewById(R.id.tvTopNavTitle);
        tvTopNavTitle.setText(navBarTitle);

        finishActivity();
        return view;
    }

    private void finishActivity() {
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }
}