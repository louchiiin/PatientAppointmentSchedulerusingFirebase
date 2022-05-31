package com.example.patientappointmentscheduler_usingfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.fragments.bottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.fragments.topNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.model.PatientInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvAlreadyHaveAnAccount;
    private EditText etFirstName, etLastName, etEmail, etPhone, etPassword, etConfirmPassword;
    private Button btnRegister;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    PatientInfo patientInfo;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        displayTopNavBar(new topNavBarFragment("Registration"));
        backToLoginTxt();
        registerUser();
    }

    private void displayTopNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutRegistration, fragment);
        fragmentTransaction.commit();
    }

    private void registerUser() {
        //initialize
        etEmail = (EditText) findViewById(R.id.etRegisterEmail);
        etPassword = (EditText) findViewById(R.id.etRegisterPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etRegisterConfirmPassword);

        etFirstName = (EditText) findViewById(R.id.etRegisterFirstName);
        etLastName = (EditText) findViewById(R.id.etRegisterLastName);
        etPhone = (EditText) findViewById(R.id.etRegisterPhone);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        //realtime database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("PatientInfo");
        //model
        patientInfo = new PatientInfo();

        btnRegister.setOnClickListener(view -> {
            //add in authentication firebase
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            //add in firestore db
            String firstName = etFirstName.getText().toString();
            String lastName = etLastName.getText().toString();
            String phone = etPhone.getText().toString();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Password does not match, please try again",
                        Toast.LENGTH_SHORT).show();
                etPassword.setError("Password does not match");
                etConfirmPassword.setError("Password does not match");
                etPassword.requestFocus();
            } else if (email.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                etEmail.setError("Required");
                etEmail.requestFocus();
            } else if (password.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                etPassword.setError("Required");
                etPassword.requestFocus();
            } else if (password.length() < 6){
                Toast.makeText(RegisterActivity.this, "Password is too short:\r6 or more characters is needed", Toast.LENGTH_SHORT).show();
                etPassword.setError("Password is too short: 6 or more is needed");
                etPassword.requestFocus();
            } else if (confirmPassword.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Confirm password is required", Toast.LENGTH_SHORT).show();
                etConfirmPassword.setError("Required");
                etConfirmPassword.requestFocus();
            } else if (!HelperUtilities.isValidEmail(etEmail.getText().toString())){
                Toast.makeText(RegisterActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                etEmail.setError("Please enter a valid email");
                etEmail.requestFocus();
            } else if (!HelperUtilities.isValidPhone(etPhone.getText().toString())){
                Toast.makeText(RegisterActivity.this, "Please enter a valid phone number \n" +
                        "ex: 09291234567", Toast.LENGTH_SHORT).show();
                etPhone.setError("Please enter a valid phone number \rex: 09291234567");
                etPhone.requestFocus();
            } else {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                try {
                                    if (task.isSuccessful()) {
                                        FirebaseUser getRegisteredUser = FirebaseAuth.getInstance().getCurrentUser();
                                        String getUid = getRegisteredUser.getUid();
                                        //progress dialog bar
                                        dialog = new ProgressDialog(RegisterActivity.this);
                                        dialog.setTitle("Loading..");
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                dialog.dismiss();
                                                addPatientInfo(getUid, firstName, lastName, phone);
                                            }
                                        }, 1500);
                                    } else {
                                        Toast.makeText(RegisterActivity.this,
                                                "Email already exists", Toast.LENGTH_SHORT).show();
                                        etEmail.setError("Email already exists");
                                        etEmail.requestFocus();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(RegisterActivity.this, "ERROR: " + e, Toast.LENGTH_SHORT).show();
                                }

                        }
                });
            }
        });
    }

    private void addPatientInfo(String uid, String firstName, String lastName, String phone) {
        patientInfo.setUid(uid);
        patientInfo.setFirstName(firstName);
        patientInfo.setLastName(lastName);
        patientInfo.setPhone(phone);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(patientInfo.getUid()).setValue(patientInfo);
                Toast.makeText(RegisterActivity.this,
                        "Successful Registration!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    private void backToLoginTxt() {
        tvAlreadyHaveAnAccount = findViewById(R.id.tvAlreadyHaveAnAccount);
        tvAlreadyHaveAnAccount.setOnClickListener(view -> {
            Intent intentToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intentToLogin);
            finish();
        });
    }


    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}