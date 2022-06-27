package com.example.patientappointmentscheduler_usingfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        inputEmail = findViewById(R.id.etUserEmail);
        inputPassword = findViewById(R.id.etUserPassword);
        tvLinkToRegister = findViewById(R.id.tvRegistrationLink);
        btnLogin = findViewById(R.id.btnLogin);
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

            if (TextUtils.isEmpty(getEmail)){
                Snackbar.make(findViewById(android.R.id.content),"Email is empty",Snackbar.LENGTH_SHORT).show();
                inputEmail.setError("Email should not be empty");
                inputEmail.requestFocus();
                return;
            } else if (TextUtils.isEmpty(getPassword)){
                Snackbar.make(findViewById(android.R.id.content),"Password is empty",Snackbar.LENGTH_SHORT).show();
                inputPassword.setError("Password should not be empty");
                inputPassword.requestFocus();
                return;
            } else if (getPassword.length() < 6) {
                Snackbar.make(findViewById(android.R.id.content),"Password needs 6 or more characters",Snackbar.LENGTH_SHORT).show();
                inputPassword.setError("Password needs 6 or more characters");
                inputPassword.requestFocus();
                return;
            }
            mAuth.signInWithEmailAndPassword(getEmail, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //progress dialog bar
                                Log.v(TAG, "signInWithCredential:success");
                                dialog.setTitle(R.string.loading_dialog);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setCancelable(false);
                                dialog.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        dialog.dismiss();
                                        Intent loggedIn = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(loggedIn);
                                        finish();
                                    }
                                }, 2000); // 3000 milliseconds delay
                            } else {
                                Log.v(TAG, "signInWithCredential:failure", task.getException());
                                Snackbar.make(findViewById(android.R.id.content),"Invalid credentials, please try again!",Snackbar.LENGTH_SHORT).show();
                                inputEmail.setError("Invalid credentials, please try again");
                                inputPassword.setError("Invalid credentials, please try again");
                                inputEmail.requestFocus();
                                inputPassword.requestFocus();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Error " + e, Toast.LENGTH_SHORT).show();
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
        } else {
            Snackbar.make(findViewById(android.R.id.content),"Please re-login",Snackbar.LENGTH_SHORT).show();
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