package com.example.patientappointmentscheduler_usingfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView txtLoggedInUser, btnHome, btnView, btnReservation, btnSchedule, btnLogout;
    private Button btnWebsite, btnEmail, btnPhone, btnFacebook;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //progress dialog bar
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading..");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
                profileInfo();
            }
        }, 1500); //


        clickHome();
        clickView();
        clickReservationsButton();
        clickScheduleButton();
        clickLogoutButton();
        clickEmailButton();
        clickWebButton();
        clickPhoneButton();
        clickFacebookButton();
    }

    private void clickFacebookButton() {
        btnFacebook = findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://facebook.com/";
                Intent fbSite = new Intent(Intent.ACTION_VIEW);
                fbSite.setData(Uri.parse(url));
                startActivity(fbSite);
            }
        });
    }

    private void clickHome() {
        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMain = new Intent(MainActivity.this, MainActivity.class);
                startActivity(goToMain);
            }
        });
    }

    private void clickView() {
        btnView = findViewById(R.id.btnViewPersonalInfo);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToUpdateProfile = new Intent(MainActivity.this, PersonalInformationActivity.class);
                startActivity(goToUpdateProfile);
            }
        });
    }

    private void clickReservationsButton() {
        btnReservation = findViewById(R.id.btnReservation);
        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToReservation = new Intent(MainActivity.this, PatientReservationActivity.class);
                startActivity(goToReservation);
            }
        });
    }

    private void clickScheduleButton() {
        btnSchedule = findViewById(R.id.btnSchedule);
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSchedule = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(goToSchedule);
            }
        });
    }

    private void clickLogoutButton() {
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Alert")
                        .setMessage("Are you sure you want to exit?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                FirebaseAuth.getInstance().signOut();
                                Intent loggedOut = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(loggedOut);
                                setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                                finish();
                            }
                        }).create().show();
            }
        });
    }

    private void profileInfo() {
        String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidReference = rootReference.child("PatientInfo").child(currentFirebaseUser);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = snapshot.child("firstName").getValue(String.class);
                String lastName = snapshot.child("lastName").getValue(String.class);
                txtLoggedInUser = findViewById(R.id.txtLoggedInUser);

                txtLoggedInUser.setText(firstName + " " + lastName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ERROR", error.getMessage());
            }
        };
        uidReference.addListenerForSingleValueEvent(eventListener);
    }

    private void clickPhoneButton() {
        btnPhone = findViewById(R.id.btnPhone);
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "123456";
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)));
                startActivity(dialIntent);
            }
        });
    }

    private void clickWebButton() {
        btnWebsite = findViewById(R.id.btnWebsite);
        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://fdc-inc.com/";
                Intent webSite = new Intent(Intent.ACTION_VIEW);
                webSite.setData(Uri.parse(url));
                startActivity(webSite);
            }
        });
    }

    private void clickEmailButton() {
        btnEmail = findViewById(R.id.btnEmail);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create an intent object
                String subject = "This is only a TEST";
                String body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas eu quam eu nulla congue feugiat. ";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + subject + "&body=" + body);
                intent.setData(data);
                startActivity(intent);
            }
        });
    }


    //onBackPressed
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Alert")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        FirebaseAuth.getInstance().signOut();
                        Intent loggedOut = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loggedOut);
                        setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                        finish();
                    }
                }).create().show();
    }
}