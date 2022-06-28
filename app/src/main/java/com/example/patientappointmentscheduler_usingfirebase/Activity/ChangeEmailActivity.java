package com.example.patientappointmentscheduler_usingfirebase.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.patientappointmentscheduler_usingfirebase.Fragments.BottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.TopNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText mAuthEmail, mAuthPassword, mChangeEmail, mChangePassword;
    private Button mAuthenticateBtn, mChangeEmailPassword;
    private LinearLayout changeEmailLayout;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        animateLoading();
        //initialize
        mAuthEmail = findViewById(R.id.etAuthEmail);
        mAuthPassword = findViewById(R.id.etAuthPassword);
        mChangeEmail = findViewById(R.id.etChangeEmail);
        mChangePassword = findViewById(R.id.change_password);

        mAuthenticateBtn = findViewById(R.id.btnAuthenticate);
        mChangeEmailPassword = findViewById(R.id.change_email_pass);

        changeEmailLayout = findViewById(R.id.changeEmailLayout);

        dialog = new ProgressDialog(ChangeEmailActivity.this);
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mAuthEmail.setText(currentUserEmail);

        clickAuthenticateButton();
        clickChangeEmailPass();
        displayTopNavBar(new TopNavBarFragment("Change Email", this));
        displayBottomNavBar(new BottomAppNavBarFragment(this));
    }

    private void animateLoading() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void clickChangeEmailPass() {
        mChangeEmailPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changeEmail = mChangeEmail.getText().toString().trim();
                String changePassword = mChangePassword.getText().toString().trim();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (changeEmail.isEmpty()) {
                    mChangeEmail.setError("Email is required");
                    mChangeEmail.requestFocus();
                } else if (changePassword.isEmpty()) {
                    mChangePassword.setError("Password is required");
                    mChangePassword.requestFocus();
                }else if (!HelperUtilities.isValidEmail(changeEmail)){
                    mChangeEmail.setError("Please enter a valid email");
                    mChangeEmail.requestFocus();
                } else if (!HelperUtilities.isShortPassword(changePassword)) {
                    mChangePassword.setError("Password should be more than 5 characters");
                    mChangePassword.requestFocus();
                } else {
                    dialog.setTitle(R.string.loading_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    user.updateEmail(changeEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String newUpdatedEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                mAuthEmail.setText(newUpdatedEmail);
                                user.updatePassword(changePassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            changeSuccessDialog().show();
                                            mChangeEmail.setText("");
                                            mAuthPassword.setText("");
                                            mChangePassword.setText("");
                                            mAuthPassword.setEnabled(true);
                                            mAuthenticateBtn.setEnabled(true);
                                            changeEmailLayout.setVisibility(View.GONE);
                                        } else {
                                            dialog.dismiss();
                                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Incorrect Credentials", Snackbar.LENGTH_LONG);
                                            snackbar.setTextColor(ContextCompat.getColor(ChangeEmailActivity.this,R.color.white));
                                            snackbar.setBackgroundTint(ContextCompat.getColor(ChangeEmailActivity.this,R.color.red));
                                            snackbar.show();
                                            mChangePassword.setError("Incorrect Credentials");
                                            mChangePassword.requestFocus();
                                        }
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Email already exists, please try another email!", Snackbar.LENGTH_LONG);
                                snackbar.setTextColor(ContextCompat.getColor(ChangeEmailActivity.this,R.color.white));
                                snackbar.setBackgroundTint(ContextCompat.getColor(ChangeEmailActivity.this,R.color.red));
                                snackbar.show();
                                mChangeEmail.setError("Email already exists!");
                                mChangeEmail.requestFocus();
                            }
                        }
                    });
                }
            }
        });
    }

    public void clickAuthenticateButton() {
        mAuthenticateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mAuthEmail.getText().toString().trim();
                String password = mAuthPassword.getText().toString().trim();
                if (password.isEmpty()) {
                    mAuthPassword.setError("Password is required");
                    mAuthPassword.requestFocus();
                } else if (password.length() < 6) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Password is incorrect", Snackbar.LENGTH_LONG);
                    snackbar.setTextColor(ContextCompat.getColor(ChangeEmailActivity.this,R.color.white));
                    snackbar.setBackgroundTint(ContextCompat.getColor(ChangeEmailActivity.this,R.color.red));
                    snackbar.show();
                    mAuthPassword.setError("Password is incorrect");
                    mAuthPassword.requestFocus();
                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    dialog.setTitle(R.string.loading_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    assert user != null;
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Authenticated Successfully", Snackbar.LENGTH_LONG);
                                snackbar.setTextColor(ContextCompat.getColor(ChangeEmailActivity.this,R.color.white));
                                snackbar.setBackgroundTint(ContextCompat.getColor(ChangeEmailActivity.this,R.color.lime_green));
                                snackbar.show();
                                changeEmailLayout.setVisibility(View.VISIBLE);
                                mAuthPassword.setEnabled(false);
                                mAuthenticateBtn.setEnabled(false);
                                mChangeEmail.setText(email);
                            }else{
                                dialog.dismiss();
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Password is incorrect", Snackbar.LENGTH_LONG);
                                snackbar.setTextColor(ContextCompat.getColor(ChangeEmailActivity.this,R.color.white));
                                snackbar.setBackgroundTint(ContextCompat.getColor(ChangeEmailActivity.this,R.color.red));
                                snackbar.show();
                                mAuthPassword.setError("Password is incorrect");
                                mAuthPassword.requestFocus();
                            }
                        }
                    });
                }
            }
        });
    }

    private Dialog changeSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeEmailActivity.this);
        builder.setMessage("Update Successfully")
                .setTitle("Success")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
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

    //onBackPressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}