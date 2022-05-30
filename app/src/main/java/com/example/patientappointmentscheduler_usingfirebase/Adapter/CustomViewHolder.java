package com.example.patientappointmentscheduler_usingfirebase.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientappointmentscheduler_usingfirebase.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    TextView tvCategoryTitle, tvSource;
    ImageView ivImgHeadline;
    CardView cardView;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);

        tvCategoryTitle = itemView.findViewById(R.id.tvCategoryTitle);
        tvSource = itemView.findViewById(R.id.tvSource);
        ivImgHeadline = itemView.findViewById(R.id.ivImgHeadline);
        cardView = itemView.findViewById(R.id.main_container);
    }
}
