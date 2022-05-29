package com.example.patientappointmentscheduler_usingfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.patientappointmentscheduler_usingfirebase.fragments.bottom_app_nav_fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView txtLoggedInUser;
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

        displayBottomNavBar(new bottom_app_nav_fragment());

        clickEmailButton();
        clickWebButton();
        clickPhoneButton();
        clickFacebookButton();
    }

    private void displayBottomNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutMainBottomAppNavBar, fragment);
        fragmentTransaction.commit();
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