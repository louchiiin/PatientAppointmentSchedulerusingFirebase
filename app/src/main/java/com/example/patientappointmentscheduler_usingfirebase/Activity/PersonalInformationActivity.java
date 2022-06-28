package com.example.patientappointmentscheduler_usingfirebase.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.patientappointmentscheduler_usingfirebase.Fragments.BottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.TopNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.model.PatientInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PersonalInformationActivity extends AppCompatActivity {

    private Button btnUpdate, btnSave, btnCancel, btnChangeEmail;

    private EditText etUpdateFirstName, etUpdateLastName,
            etUpdatePhone;

    private LinearLayout layoutSave, layoutUpdate;

    PatientInfo patientInfo;

    ProgressDialog dialog;
    //realtime database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        animateLoading();

        etUpdateFirstName = findViewById(R.id.etUpdateFirstName);
        etUpdateLastName = findViewById(R.id.etUpdateLastName);
        etUpdatePhone = findViewById(R.id.etUpdatePhone);
        //initialize
        layoutSave = findViewById(R.id.layoutSave);
        layoutUpdate = findViewById(R.id.layoutUpdate);
        btnChangeEmail = findViewById(R.id.btnChangeEmail);

        displayTopNavBar(new TopNavBarFragment("Personal Information", this));
        displayBottomNavBar(new BottomAppNavBarFragment(this));

        dialog = new ProgressDialog(PersonalInformationActivity.this);
        dialog.setTitle(R.string.loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        displayUserValues();
        clickChangeEmail();
        clickUpdateButton();
        clickCancelButton();
        clickSave();
    }

    private void animateLoading() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void clickChangeEmail() {
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeEmail = new Intent(PersonalInformationActivity.this, ChangeEmailActivity.class);
                startActivity(changeEmail);
            }
        });
    }


    private void displayTopNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.patientInformationTopNav, fragment);
        fragmentTransaction.commit();
    }

    private void displayBottomNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutPersonalInfoBottomAppNavBar, fragment);
        fragmentTransaction.commit();
    }

    private void displayUserValues() {
        String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidReference = rootReference.child("PatientInfo").child(currentFirebaseUser);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.dismiss();
                String firstName = snapshot.child("firstName").getValue(String.class);
                String lastName = snapshot.child("lastName").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                etUpdateFirstName.setText(firstName);
                etUpdateLastName.setText(lastName);
                etUpdatePhone.setText(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DISPLAYUSERERR:", error.getMessage());
            }
        };
        uidReference.addListenerForSingleValueEvent(eventListener);
    }

    private void clickSave() {
        //realtime database
        String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //model
        patientInfo = new PatientInfo();

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO update to hashmap update
                String firstName = etUpdateFirstName.getText().toString();
                String lastName = etUpdateLastName.getText().toString();
                String phone = etUpdatePhone.getText().toString();

                if (firstName.isEmpty()) {
                    emptyFirstNameDialog().show();
                    etUpdateFirstName.setError("First Name is required");
                    etUpdateFirstName.requestFocus();
                } else if (lastName.isEmpty()) {
                    emptyLastNameDialog().show();
                    etUpdateFirstName.setError("First Name is required");
                    etUpdateFirstName.requestFocus();
                } else if (phone.isEmpty()) {
                    emptyPhoneDialog().show();
                    etUpdatePhone.setError("Phone is required");
                    etUpdatePhone.requestFocus();
                } else if (!HelperUtilities.isValidPhone(phone)) {
                    etUpdatePhone.setError("Please enter a valid phone number \nex: 09291234567");
                    etUpdatePhone.requestFocus();
                } else {
                    //progress dialog bar
                    dialog.setTitle(R.string.loading_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    etUpdateFirstName.setEnabled(false);
                    etUpdateLastName.setEnabled(false);
                    etUpdatePhone.setEnabled(false);
                    //layout gone/visible
                    layoutUpdate.setVisibility(View.VISIBLE);
                    layoutSave.setVisibility(View.GONE);

                    updatePatientInfo(currentFirebaseUser, firstName, lastName, phone);

                }
            }
        });

    }

    private Dialog emptyPhoneDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformationActivity.this);
        builder.setMessage("Phone should not be empty, please input field")
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    private Dialog emptyLastNameDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformationActivity.this);
        builder.setMessage("Last Name should not be empty, please input field")
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    private Dialog emptyFirstNameDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformationActivity.this);
        builder.setMessage("First Name should not be empty, please input field")
                .setTitle("Alert")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    private Dialog addUpdateSuccessDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInformationActivity.this);
        builder.setMessage("Update Successfully")
                .setTitle("Success")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    private void updatePatientInfo(String currentFirebaseUser, String firstName, String lastName, String phone) {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidReference = rootReference.child("PatientInfo");

        HashMap hashMap = new HashMap();
        hashMap.put("firstName", firstName);
        hashMap.put("lastName", lastName);
        hashMap.put("phone", phone);
        uidReference.child(currentFirebaseUser).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                dialog.dismiss();
                addUpdateSuccessDialog().show();
            }
        });
    }

    private void clickCancelButton() {
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayUserValues();
                etUpdateFirstName.setEnabled(false);
                etUpdateLastName.setEnabled(false);
                etUpdatePhone.setEnabled(false);
                //layout gone/visible
                layoutUpdate.setVisibility(View.VISIBLE);
                layoutSave.setVisibility(View.GONE);
            }
        });
    }

    private void clickUpdateButton() {
        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set edit text to enabled
                etUpdateFirstName.setSingleLine(true);
                etUpdateFirstName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                etUpdateFirstName.setEnabled(true);
                etUpdateLastName.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                etUpdateLastName.setSingleLine(true);
                etUpdateLastName.setEnabled(true);
                etUpdatePhone.setImeOptions(EditorInfo.IME_ACTION_DONE);
                etUpdatePhone.setSingleLine(true);
                etUpdatePhone.setEnabled(true);
                //layout gone/visible
                layoutUpdate.setVisibility(View.GONE);
                layoutSave.setVisibility(View.VISIBLE);
            }
        });
    }

    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}