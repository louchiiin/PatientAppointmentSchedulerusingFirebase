package com.example.patientappointmentscheduler_usingfirebase.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.patientappointmentscheduler_usingfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_STATUS";
    //FireBase global
    private FirebaseAuth mAuth;
    //global declarations
    private EditText inputEmail, inputPassword;
    private TextView tvLinkToRegister;
    private Button btnLogin;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        animateLoading();
        //firebase
        mAuth = FirebaseAuth.getInstance();
        //castings
        inputEmail = findViewById(R.id.login_email);
        inputPassword = findViewById(R.id.login_password);
        tvLinkToRegister = findViewById(R.id.registration_link);
        btnLogin = findViewById(R.id.login_button);
        dialog = new ProgressDialog(LoginActivity.this);
        clickedLoginButton();
        clickLinkToRegister();
    }

    private void animateLoading() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void clickLinkToRegister() {
        tvLinkToRegister.setOnClickListener(view -> {
            Intent loggedIn = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(loggedIn);
        });
    }

    private void clickedLoginButton() {
        btnLogin.setOnClickListener(view -> {
            String getEmail = inputEmail.getText().toString().trim();
            String getPassword = inputPassword.getText().toString().trim();
            dialog.setTitle(R.string.loading_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Hide keyboard
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            if (TextUtils.isEmpty(getEmail) || !HelperUtilities.isValidEmail(getEmail)){
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Email is incorrect!", Snackbar.LENGTH_SHORT);
                snackbar.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.white));
                snackbar.setBackgroundTint(ContextCompat.getColor(LoginActivity.this, R.color.red));
                snackbar.show();
                inputEmail.setError("Email is incorrect");
                inputEmail.requestFocus();
                return;
            } else if (getPassword.length() < 6 || TextUtils.isEmpty(getPassword)) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Password is incorrect!", Snackbar.LENGTH_SHORT);
                snackbar.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.white));
                snackbar.setBackgroundTint(ContextCompat.getColor(LoginActivity.this, R.color.red));
                snackbar.show();
                inputPassword.setError("Password was incorrect");
                inputPassword.requestFocus();
                return;
            }
            mAuth.signInWithEmailAndPassword(getEmail, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                //progress dialog bar
                                Log.v(TAG, "signInWithCredential:success");
                                Intent loggedIn = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(loggedIn);
                                finish();
                            } else {
                                Log.v(TAG, "signInWithCredential:failure", task.getException());
                                dialog.dismiss();
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Invalid credentials, please try again", Snackbar.LENGTH_SHORT);
                                snackbar.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.white));
                                snackbar.setBackgroundTint(ContextCompat.getColor(LoginActivity.this, R.color.red));
                                snackbar.show();
                                inputEmail.setError("Invalid credentials, please try again");
                                inputPassword.setError("Invalid credentials, please try again");
                                inputEmail.requestFocus();
                                inputPassword.requestFocus();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            //Toast.makeText(LoginActivity.this, "Error " + e, Toast.LENGTH_SHORT).show();
                            Log.e("loginError", "error:" + e);
                        }
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        }
    }

    //onBackPressed
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(LoginActivity.this)
        .setTitle("Alert")
        .setMessage("Are you sure you want to exit?")
        .setNegativeButton(android.R.string.no, null)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                finish();
            }
        }).create().show();
    }
}