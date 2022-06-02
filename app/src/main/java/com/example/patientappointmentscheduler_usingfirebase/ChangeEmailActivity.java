package com.example.patientappointmentscheduler_usingfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.fragments.bottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.fragments.topNavBarFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText mAuthEmail, mAuthPassword, mChangeEmail;
    private Button mAuthenticateBtn, mChangeEmailBtn;

    private LinearLayout changeEmailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        //initialize
        mAuthEmail = findViewById(R.id.etAuthEmail);
        mAuthPassword = findViewById(R.id.etAuthPassword);
        mChangeEmail = findViewById(R.id.etChangeEmail);

        mAuthenticateBtn = findViewById(R.id.btnAuthenticate);
        mChangeEmailBtn = findViewById(R.id.btnChangeEmailAddress);

        changeEmailLayout = findViewById(R.id.changeEmailLayout);

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mAuthEmail.setText(currentUserEmail);

        clickAuthenticateButton();
        clickChangeEmail();
        displayTopNavBar(new topNavBarFragment("Change Email"));
        displayBottomNavBar(new bottomAppNavBarFragment());
    }

    private void clickChangeEmail() {
        mChangeEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO validations before success
                String changeEmail = mChangeEmail.getText().toString().trim();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.updateEmail(changeEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangeEmailActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            mChangeEmail.setText("");
                            mAuthPassword.setText("");
                            mAuthPassword.setEnabled(true);
                            mAuthenticateBtn.setEnabled(true);
                            changeEmailLayout.setVisibility(View.GONE);
                            startActivity(new Intent(ChangeEmailActivity.this, ChangeEmailActivity.class));
                            finish();
                        } else {
                            Toast.makeText(ChangeEmailActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void clickAuthenticateButton() {
        mAuthenticateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mAuthEmail.getText().toString().trim();
                String password = mAuthPassword.getText().toString().trim();
                //TODO add validation if password is incorrect
                if (password.isEmpty()) {
                    Toast.makeText(ChangeEmailActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    mAuthPassword.setError("Password is required");
                    mAuthPassword.requestFocus();
                } else if (password.length() < 6) {
                    Toast.makeText(ChangeEmailActivity.this, "Password is too short:\r6 or more characters is needed", Toast.LENGTH_SHORT).show();
                    mAuthPassword.setError("Password is too short: 6 or more is needed");
                    mAuthPassword.requestFocus();
                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ChangeEmailActivity.this, "User authenticated", Toast.LENGTH_SHORT).show();
                                changeEmailLayout.setVisibility(View.VISIBLE);
                                mAuthPassword.setEnabled(false);
                                mAuthenticateBtn.setEnabled(false);
                            }else{
                                Toast.makeText(ChangeEmailActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void displayTopNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.changeEmailTopNav, fragment);
        fragmentTransaction.commit();
    }

    private void displayBottomNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutChangeEmailBottomAppNavBar, fragment);
        fragmentTransaction.commit();
    }
}