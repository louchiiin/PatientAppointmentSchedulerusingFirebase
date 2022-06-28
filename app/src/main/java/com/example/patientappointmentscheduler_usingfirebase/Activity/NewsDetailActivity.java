package com.example.patientappointmentscheduler_usingfirebase.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.patientappointmentscheduler_usingfirebase.Fragments.BottomAppNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.Fragments.TopNavBarFragment;
import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.model.NewsHeadlines;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends AppCompatActivity {
    NewsHeadlines headlines;
    private TextView tvNewsDetailsTitle, tvNewsAuthor, tvNewsTime, tvNewsDetails, tvNewsContent;
    private ImageView ivDetailNewsImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        //initialize
        tvNewsDetailsTitle = findViewById(R.id.tvNewsDetailsTitle);
        tvNewsAuthor = findViewById(R.id.tvNewsAuthor);
        tvNewsTime = findViewById(R.id.tvNewsTime);
        tvNewsDetails = findViewById(R.id.tvNewsDetails);
        tvNewsContent = findViewById(R.id.tvNewsContent);
        ivDetailNewsImg = findViewById(R.id.ivDetailNewsImg);

        headlines = (NewsHeadlines) getIntent().getSerializableExtra("data");
        tvNewsDetailsTitle.setText(headlines.getTitle());
        tvNewsAuthor.setText(headlines.getAuthor());
        tvNewsTime.setText(headlines.getPublishedAt());
        tvNewsDetails.setText(headlines.getDescription());
        tvNewsContent.setText(headlines.getContent());
        Picasso.get().load(headlines.getUrlToImage()).into(ivDetailNewsImg);

        displayTopNavBar(new TopNavBarFragment("News Detail", this));
        displayBottomNavBar(new BottomAppNavBarFragment(this));
    }

    private void displayTopNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.healthNewsDetailTopNav, fragment);
        fragmentTransaction.commit();
    }

    private void displayBottomNavBar(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.healthNewsDetailBottomAppNavBar, fragment);
        fragmentTransaction.commit();
    }
}