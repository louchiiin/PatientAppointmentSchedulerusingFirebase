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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public topNavBarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment topNavBarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static topNavBarFragment newInstance(String param1, String param2) {
        topNavBarFragment fragment = new topNavBarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_top_nav_bar, container, false);

        tvBack = view.findViewById(R.id.tvBack);
        tvTopNavTitle = view.findViewById(R.id.tvTopNavTitle);
        setTitle();
        finishActivity();
        return view;
    }

    private void setTitle() {
        // TODO set title based on Activity

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