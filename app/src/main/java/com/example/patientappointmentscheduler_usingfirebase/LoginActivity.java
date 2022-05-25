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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        //firebase
        mAuth = FirebaseAuth.getInstance();
        //castings
        inputEmail = findViewById(R.id.etUserEmail);
        inputPassword = findViewById(R.id.etUserPassword);
        tvLinkToRegister = findViewById(R.id.tvRegistrationLink);
        btnLogin = findViewById(R.id.btnLogin);

        clickedLoginButton();
        clickLinkToRegister();
        //only for deletion after app is launched
        /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query loginQuery = ref.child("PatientInfo").orderByChild("firstName").equalTo("test");

        loginQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });*/
    }

    private void clickLinkToRegister() {
        tvLinkToRegister.setOnClickListener(view -> {
            Intent loggedIn = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(loggedIn);
            finish();
        });
    }

    private void clickedLoginButton() {
        btnLogin.setOnClickListener(view -> {
            String getEmail = inputEmail.getText().toString().trim();
            String getPassword = inputPassword.getText().toString().trim();

            if (TextUtils.isEmpty(getEmail)){
                Toast.makeText(LoginActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                inputEmail.setError("Email should not be empty");
                inputEmail.requestFocus();
                return;
            } else if (TextUtils.isEmpty(getPassword)){
                Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                inputPassword.setError("Password should not be empty");
                inputPassword.requestFocus();
                return;
            } else if (getPassword.length() < 6) {
                Toast.makeText(LoginActivity.this, "Password needs 6 or more characters", Toast.LENGTH_SHORT).show();
                inputPassword.setError("Password needs 6 or more characters");
                inputPassword.requestFocus();
                return;
            }
            /*FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();*/
            mAuth.signInWithEmailAndPassword(getEmail, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //progress dialog bar
                                Log.d(TAG, "signInWithCredential:success");
                                Intent loggedIn = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(loggedIn);
                                finish();
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Invalid credentials, please try again",
                                        Toast.LENGTH_SHORT).show();
                                inputEmail.setError("Invalid credentials, please try again");
                                inputPassword.setError("Invalid credentials, please try again");
                                inputEmail.requestFocus();
                                inputPassword.requestFocus();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(LoginActivity.this,
                    "Please re-log in again!", Toast.LENGTH_SHORT).show();
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