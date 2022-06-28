package com.example.patientappointmentscheduler_usingfirebase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.Activity.ReservationInfoActivity;
import com.example.patientappointmentscheduler_usingfirebase.model.ReservationList;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private Context mContext;
    public View mView;
    private ArrayList<ReservationList> mReservationList;

    public ReservationAdapter(Context context, ArrayList<ReservationList> list) {
        this.mContext = context;
        this.mReservationList = list;
    }

    @NonNull
    @Override
    public ReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.reservation_list_item, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationAdapter.ViewHolder holder, int position) {
        ReservationList reservationList = mReservationList.get(position);

        holder.tvCategoryName.setText(reservationList.getAppointmentCategory());
        holder.tvDoctorsName.setText(reservationList.getDoctorsName());
        holder.tvPatientsName.setText(reservationList.getPatientsName());
        holder.tvReservationScheduleDateTime.setText(reservationList.getAppointmentDateTime());
        holder.tvCreatedDate.setText(reservationList.getCurrentDate());
        holder.tvReservationID.setText(reservationList.getReservationID());
    }

    @Override
    public int getItemCount() {
        return mReservationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvDoctorsName, tvPatientsName, tvReservationScheduleDateTime, tvCreatedDate, tvReservationID;
        LinearLayout lvReservationItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDoctorsName = itemView.findViewById(R.id.tvDoctorsName);
            tvPatientsName = itemView.findViewById(R.id.tvPatientsName);
            tvReservationScheduleDateTime = itemView.findViewById(R.id.tvReservationScheduleDateTime);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            tvReservationID = itemView.findViewById(R.id.tvReservationID);

            lvReservationItem = itemView.findViewById(R.id.list_reservation_item);

            lvReservationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //values to string
                    String categoryName = tvCategoryName.getText().toString();
                    String doctorsName = tvDoctorsName.getText().toString();
                    String patientsName = tvPatientsName.getText().toString();
                    String reservationScheduleDateTime = tvReservationScheduleDateTime.getText().toString();
                    String createdDate = tvCreatedDate.getText().toString();
                    String reservationID = tvReservationID.getText().toString();

                    Intent intent = new Intent(view.getContext(), ReservationInfoActivity.class);
                    intent.putExtra("CATEGORY NAME", categoryName);
                    intent.putExtra("DOCTORS NAME", doctorsName);
                    intent.putExtra("PATIENTS NAME", patientsName);
                    intent.putExtra("SCHEDULE DATETIME", reservationScheduleDateTime);
                    intent.putExtra("CREATED DATE", createdDate);
                    intent.putExtra("RESERVATION ID", reservationID);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
