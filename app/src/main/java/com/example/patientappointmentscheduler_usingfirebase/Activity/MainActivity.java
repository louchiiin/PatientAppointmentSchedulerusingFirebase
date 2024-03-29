package com.example.patientappointmentscheduler_usingfirebase.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patientappointmentscheduler_usingfirebase.Adapter.CustomAdapter;
import com.example.patientappointmentscheduler_usingfirebase.Interfaces.OnFetchDataListener;
import com.example.patientappointmentscheduler_usingfirebase.Interfaces.SelectListener;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.BottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.model.NewsApiResponse;
import com.example.patientappointmentscheduler_usingfirebase.model.NewsHeadlines;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SelectListener, View.OnClickListener{

    private TextView txtLoggedInUser;
    private Button btnWebsite, btnEmail, btnPhone, btnFacebook;
    private ProgressDialog dialog, newsDialog;
    //private Button mBusinessButton, mEntertainmentButton, mGeneralButton, mHealthRefresh, mScienceButton, mSportsButton, mTechnologyButton;
    private TextView mHealthRefresh;
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private SearchView searchView;
    private SwipeRefreshLayout mSwipeRefreshMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkNetworkConnection();
        if (checkNetworkConnection()){
            Log.d("LOUCHIN", "connection status=OK");
            animateLoading();
            mSwipeRefreshMain = findViewById(R.id.main_swipe_refresh);
            searchView = findViewById(R.id.search_news);
            /*mBusinessButton = findViewById(R.id.btnBusiness);
            mEntertainmentButton = findViewById(R.id.btnEntertainment);
            mGeneralButton = findViewById(R.id.btnGeneral);*/
            mHealthRefresh = findViewById(R.id.refresh_health_news);
            /*mScienceButton = findViewById(R.id.btnScience);
            mSportsButton = findViewById(R.id.btnSports);
            mTechnologyButton = findViewById(R.id.btnTechnology);*/

            /*mBusinessButton.setOnClickListener(this);
            mEntertainmentButton.setOnClickListener(this);
            mGeneralButton.setOnClickListener(this);*/
            mHealthRefresh.setOnClickListener(this);
            /*mScienceButton.setOnClickListener(this);
            mSportsButton.setOnClickListener(this);
            mTechnologyButton.setOnClickListener(this);*/
            loadValues();

            mSwipeRefreshMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    checkNetworkConnection();
                    loadValues();
                    mSwipeRefreshMain.setRefreshing(false);
                }
            });

            displayBottomNavBar(new BottomAppNavBarFragment(this));
            clickEmailButton();
            clickWebButton();
            clickPhoneButton();
            clickFacebookButton();
        } else {
            newsDialog.dismiss();
            dialog.dismiss();
            Log.d("LOUCHIN", "connection status=NO INTERNET");
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Check your internet connection!", Snackbar.LENGTH_SHORT);
            snackbar.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
            snackbar.setBackgroundTint(ContextCompat.getColor(MainActivity.this, R.color.black));
            snackbar.show();
        }

    }

    private void loadValues() {
        dialog = new ProgressDialog(MainActivity.this);
        loadProfileDialog();
        newsDialog = new ProgressDialog(MainActivity.this);
        loadNewsDialog();
        searchNews();
        profileInfo();
        //health news listener
        RequestManager manager = new RequestManager(MainActivity.this);
        manager.getNewsHeadLines(listener, "health", null);
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    private void animateLoading() {
        overridePendingTransition(R.anim.popup_open_enter, R.anim.popup_open_exit);
    }

    private void searchNews() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                newsDialog.setTitle("Fetching News articles of " + query);
                newsDialog.setCanceledOnTouchOutside(false);
                newsDialog.setCancelable(false);
                newsDialog.show();
                RequestManager manager = new RequestManager(MainActivity.this);
                //manager.getNewsHeadLines(listener, "general", query);
                manager.getNewsHeadLines(listener, "Health", query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        //Button button = (Button) v;
        TextView textView = (TextView) v;
        String category = textView.getText().toString();

        //newsDialog.setTitle("Fetching news articles of " + category);
        newsDialog.setTitle("Fetching news articles of Health");
        newsDialog.setCanceledOnTouchOutside(false);
        newsDialog.setCancelable(false);
        newsDialog.show();

        RequestManager manager = new RequestManager(MainActivity.this);
        //manager.getNewsHeadLines(listener, category, null);
        manager.getNewsHeadLines(listener, "Health", null);
    }

    private void loadProfileDialog() {
        //dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.loading_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void loadNewsDialog() {
        newsDialog.setTitle("Fetching data...");
        newsDialog.setCanceledOnTouchOutside(false);
        newsDialog.setCancelable(false);
        newsDialog.show();
    }

    private final OnFetchDataListener<NewsApiResponse> listener = new OnFetchDataListener<NewsApiResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {
            if(list.isEmpty()){
                noDataFoundDialog().show();
            } else {
                showNews(list);
            }
            searchView.clearFocus();
            newsDialog.dismiss();
        }

        @Override
        public void onError(String message) {
            Toast.makeText(MainActivity.this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
        }
    };

    private Dialog noDataFoundDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("No Data Found, please try again!")
                .setTitle("Info!")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing (closes only the dialog)
                    }
                });
        return builder.create();
    }

    private void showNews(List<NewsHeadlines> list) {
        recyclerView = findViewById(R.id.health_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        customAdapter = new CustomAdapter(this, list, this);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    public void OnNewsClicked(NewsHeadlines headlines) {
        startActivity(new Intent(MainActivity.this, NewsDetailActivity.class)
                .putExtra("data", headlines));
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
                String url = "http://facebook.com/FortyDegreesCelsiusInc";
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
                //progress dialog is dismissed after loading above
                dialog.dismiss();
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