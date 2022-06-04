package com.example.patientappointmentscheduler_usingfirebase.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patientappointmentscheduler_usingfirebase.LoginActivity;
import com.example.patientappointmentscheduler_usingfirebase.MainActivity;
import com.example.patientappointmentscheduler_usingfirebase.PatientReservationActivity;
import com.example.patientappointmentscheduler_usingfirebase.PersonalInformationActivity;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.ScheduleActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link bottomAppNavBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class bottomAppNavBarFragment extends Fragment {
    private View view;
    private TextView btnHome, btnView, btnReservation, btnSchedule, btnLogout;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public bottomAppNavBarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment bottomAppNavBarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static bottomAppNavBarFragment newInstance(String param1, String param2) {
        bottomAppNavBarFragment fragment = new bottomAppNavBarFragment();
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