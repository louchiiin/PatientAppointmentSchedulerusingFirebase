package com.example.patientappointmentscheduler_usingfirebase.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patientappointmentscheduler_usingfirebase.Activity.LoginActivity;
import com.example.patientappointmentscheduler_usingfirebase.Activity.MainActivity;
import com.example.patientappointmentscheduler_usingfirebase.Activity.PatientReservationActivity;
import com.example.patientappointmentscheduler_usingfirebase.Activity.PersonalInformationActivity;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.Activity.ScheduleActivity;
import com.google.firebase.auth.FirebaseAuth;

public class BottomAppNavBarFragment extends Fragment {
    private View view;
    private TextView btnHome, btnView, btnReservation, btnSchedule, btnLogout;
    private Activity mActivity;

    public  BottomAppNavBarFragment() {
    }

    public BottomAppNavBarFragment(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bottom_app_nav, container, false);

        clickHome();
        clickView();
        clickReservationsButton();
        clickScheduleButton();
        clickLogoutButton();

        return view;
    }

    private void clickHome() {
        btnHome = view.findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMain = new Intent(getActivity(), MainActivity.class);
                startActivity(goToMain);
            }
        });
    }

    private void clickView() {
        btnView = view.findViewById(R.id.btnViewPersonalInfo);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateProfile = new Intent(getActivity(), PersonalInformationActivity.class);
                startActivity(updateProfile);
            }
        });
    }

    private void clickReservationsButton() {
        btnReservation = view.findViewById(R.id.btnReservation);
        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToReservation = new Intent(getActivity(), PatientReservationActivity.class);
                startActivity(goToReservation);
            }
        });
    }

    private void clickScheduleButton() {
        btnSchedule = view.findViewById(R.id.btnSchedule);
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSchedule = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(goToSchedule);
            }
        });
    }

    private void clickLogoutButton() {
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to exit?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                FirebaseAuth.getInstance().signOut();
                                mActivity.overridePendingTransition(R.anim.animation_leave, R.anim.animation_enter);
                                Intent loggedOut = new Intent(getActivity(), LoginActivity.class);
                                startActivity(loggedOut);
                                getActivity().setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                                getActivity().finish();
                            }
                        }).create().show();
            }
        });
    }
}