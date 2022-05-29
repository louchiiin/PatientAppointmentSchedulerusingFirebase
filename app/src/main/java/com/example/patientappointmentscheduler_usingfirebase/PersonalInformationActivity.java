package com.example.patientappointmentscheduler_usingfirebase;

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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.fragments.bottom_app_nav_fragment;
import com.example.patientappointmentscheduler_usingfirebase.model.PatientInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PersonalInformationActivity extends AppCompatActivity {
    private TextView tvUpdateBack;

    private Button btnUpdate, btnSave, btnCancel;

    private EditText etUpdateFirstName, etUpdateLastName,
    etUpdatePhone;

    private LinearLayout layoutSave, layoutUpdate;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    PatientInfo patientInfo;

    ProgressDialog dialog;
    //realtime database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        etUpdateFirstName = findViewById(R.id.etUpdateFirstName);
        etUpdateLastName = findViewById(R.id.etUpdateLastName);
        etUpdatePhone = findViewById(R.id.etUpdatePhone);
        //initialize
        layoutSave = findViewById(R.id.layoutSave);
        layoutUpdate = findViewById(R.id.layoutUpdate);

        displayBottomNavBar(new bottom_app_nav_fragment());
        displayUserValues();
        backToMain();
        clickUpdateButton();
        clickCancelButton();
        clickSave();
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
                String firstName = etUpdateFirstName.getText().toString();
                String lastName = etUpdateLastName.getText().toString();
                String phone = etUpdatePhone.getText().toString();

                if (firstName.isEmpty()){
                    emptyFirstNameDialog().show();
                    etUpdateFirstName.setError("First Name is required");
                    etUpdateFirstName.requestFocus();
                } else if (lastName.isEmpty()) {
                    emptyLastNameDialog().show();
                    etUpdateFirstName.setError("First Name is required");
                    etUpdateFirstName.requestFocus();
                } else if (phone.isEmpty()){
                    emptyPhoneDialog().show();
                    etUpdatePhone.setError("Phone is required");
                    etUpdatePhone.requestFocus();
                } else if (!HelperUtilities.isValidPhone(phone)){
                    Toast.makeText(PersonalInformationActivity.this, "Please enter a valid phone number \n" +
                            "ex: 09291234567", Toast.LENGTH_SHORT).show();
                    etUpdatePhone.setError("Please enter a valid phone number \nex: 09291234567");
                    etUpdatePhone.requestFocus();
                } else{
                    //progress dialog bar
                    dialog = new ProgressDialog(PersonalInformationActivity.this);
                    dialog.setTitle("Loading..");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            etUpdateFirstName.setEnabled(false);
                            etUpdateLastName.setEnabled(false);
                            etUpdatePhone.setEnabled(false);
                            //layout gone/visible
                            layoutUpdate.setVisibility(View.VISIBLE);
                            layoutSave.setVisibility(View.GONE);

                            //TODO: check why update 2nd time app will crash
                            updatePatientInfo(currentFirebaseUser, firstName, lastName, phone);
                        }
                    }, 1500);
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
        patientInfo.setUid(currentFirebaseUser);
        patientInfo.setFirstName(firstName);
        patientInfo.setLastName(lastName);
        patientInfo.setPhone(phone);

        uidReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uidReference.child(patientInfo.getUid()).setValue(patientInfo);
                Toast.makeText(PersonalInformationActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PersonalInformationActivity.this, PersonalInformationActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    private void clickCancelButton() {
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Fields are disabled", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(getApplicationContext(), "Fields are now editable", Toast.LENGTH_SHORT).show();
                // set edit text to enabled
                etUpdateFirstName.setEnabled(true);
                etUpdateLastName.setEnabled(true);
                etUpdatePhone.setEnabled(true);
                //layout gone/visible
                layoutUpdate.setVisibility(View.GONE);
                layoutSave.setVisibility(View.VISIBLE);
            }
        });
    }

    private void backToMain() {
        tvUpdateBack = findViewById(R.id.tvUpdateBack);
        tvUpdateBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;
                Intent backToMain = new Intent(PersonalInformationActivity.this, MainActivity.class);
                startActivity(backToMain);
                finish();
            }
        });
    }

    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PersonalInformationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}