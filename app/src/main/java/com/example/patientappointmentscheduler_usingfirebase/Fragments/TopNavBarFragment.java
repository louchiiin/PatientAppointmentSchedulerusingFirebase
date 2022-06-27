package com.example.patientappointmentscheduler_usingfirebase.Fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patientappointmentscheduler_usingfirebase.R;

public class TopNavBarFragment extends Fragment {
    private View view;
    private TextView tvBack, tvTopNavTitle;
    private String navBarTitle;
    private Activity mActivity;

    public TopNavBarFragment(){
    }

    public TopNavBarFragment(String navTitle, Activity activity) {
        // Required empty public constructor
        this.navBarTitle = navTitle;
        this.mActivity = activity;
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
                //mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                getActivity().finish();
            }
        });
    }
}