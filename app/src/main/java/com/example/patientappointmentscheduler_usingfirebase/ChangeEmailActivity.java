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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.Fragments.BottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.TopNavBarFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
        displayTopNavBar(new TopNavBarFragment("Change Email"));
        displayBottomNavBar(new BottomAppNavBarFragment());
    }

    private void clickChangeEmailPass() {
        mChangeEmailPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changeEmail = mChangeEmail.getText().toString().trim();
                String changePassword = mChangePassword.getText().toString().trim();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (changeEmail.isEmpty() || changePassword.isEmpty()) {
                    mChangeEmail.setError("Email is required");
                    mChangePassword.setError("Password is required");
                    mChangeEmail.requestFocus();
                    mChangePassword.requestFocus();
                } else if (!HelperUtilities.isValidEmail(changeEmail)){
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
                                        }
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                Toast.makeText(ChangeEmailActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
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
                //TODO add validation if password is incorrect
                if (password.isEmpty()) {
                    mAuthPassword.setError("Password is required");
                    mAuthPassword.requestFocus();
                } else if (password.length() < 6) {
                    mAuthPassword.setError("Password is too short: 6 or more is needed");
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
                                changeEmailLayout.setVisibility(View.VISIBLE);
                                mAuthPassword.setEnabled(false);
                                mAuthenticateBtn.setEnabled(false);
                                mChangeEmail.setText(email);
                            }else{
                                dialog.dismiss();
                                Toast.makeText(ChangeEmailActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private Dialog changeSuccessDialog() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(ChangeEmailActivity.this);
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
}