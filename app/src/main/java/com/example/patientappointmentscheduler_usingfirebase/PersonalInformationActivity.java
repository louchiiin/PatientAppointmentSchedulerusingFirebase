package com.example.patientappointmentscheduler_usingfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.fragments.bottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.fragments.topNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.model.PatientInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalInformationActivity extends AppCompatActivity {
    private TextView tvUpdateBack;

    private Button btnUpdate, btnSave, btnCancel, btnChangeEmail;

    private EditText etUpdateFirstName, etUpdateLastName,
            etUpdatePhone;

    private LinearLayout layoutSave, layoutUpdate;

    private AlertDialog.Builder changeEmailDialog;
    LayoutInflater layoutInflater;

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
        btnChangeEmail = findViewById(R.id.btnChangeEmail);



        displayTopNavBar(new topNavBarFragment());
        displayBottomNavBar(new bottomAppNavBarFragment());
        displayUserValues();
        clickChangeEmail();
        clickUpdateButton();
        clickCancelButton();
        clickSave();
    }

    private void clickChangeEmail() {
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeEmail = new Intent(PersonalInformationActivity.this, ChangeEmailActivity.class);
                startActivity(changeEmail);

                /*
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(PersonalInformationActivity.this);
                alertdialog.setTitle(R.string.change_email_auth);

                Typeface typeface = ResourcesCompat.getFont(PersonalInformationActivity.this, R.font.ubuntu_regular);

                final EditText authEmail = new EditText(PersonalInformationActivity.this);
                final EditText authPassword = new EditText(PersonalInformationActivity.this);

                //firebase get email
                String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                authEmail.setText(currentEmail);
                authEmail.setHint("Current Email");
                authEmail.setTypeface(typeface);
                authEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                authEmail.setTextSize(16);
                authEmail.setMaxLines(1);
                authEmail.setBackgroundResource(R.drawable.custom_input);
                authEmail.setPadding(30,30,30, 30);

                authPassword.setHint("Current Password");
                authPassword.setTypeface(typeface);
                authPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                authPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                authPassword.setTextSize(16);
                authPassword.setMaxLines(1);
                authPassword.setBackgroundResource(R.drawable.custom_input);
                authPassword.setPadding(30,30,30, 30);

                //set up in a linear layout
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20, 20, 20, 20); //set margin

                LinearLayout lp = new LinearLayout(getApplicationContext());
                lp.setOrientation(LinearLayout.VERTICAL);

                lp.addView(authEmail, layoutParams);
                lp.addView(authPassword, layoutParams);
                alertdialog.setView(lp);
                alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String currentEmail = authEmail.getText().toString().trim();
                        String currentPassword = authPassword.getText().toString().trim();

                        if (currentEmail.isEmpty()) {
                            Toast.makeText(PersonalInformationActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                            authEmail.setError("Required");
                            return;
                        } else if (currentPassword.isEmpty()){
                            Toast.makeText(PersonalInformationActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                            authPassword.setError("Required");
                            authPassword.requestFocus();
                            return;
                        } else if (currentPassword.length() < 6){
                            Toast.makeText(PersonalInformationActivity.this, "Password is too short:\r6 or more characters is needed", Toast.LENGTH_SHORT).show();
                            authPassword.setError("Password is too short: 6 or more is needed");
                            authPassword.requestFocus();
                            return;
                        } else if (!HelperUtilities.isValidEmail(currentEmail)) {
                            Toast.makeText(PersonalInformationActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                            authEmail.setError("Please enter a valid email");
                            authEmail.requestFocus();
                        } else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, currentPassword);

                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(PersonalInformationActivity.this, "dialog should appear", Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.updateEmail("fdc.louchintest@gmail.com").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(PersonalInformationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(PersonalInformationActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            dialogInterface.dismiss();
                        }
                    }
                });

                alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alert = alertdialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
*/
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
                    Toast.makeText(PersonalInformationActivity.this, "Please enter a valid phone number \n" +
                            "ex: 09291234567", Toast.LENGTH_SHORT).show();
                    etUpdatePhone.setError("Please enter a valid phone number \nex: 09291234567");
                    etUpdatePhone.requestFocus();
                } else {
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

    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}