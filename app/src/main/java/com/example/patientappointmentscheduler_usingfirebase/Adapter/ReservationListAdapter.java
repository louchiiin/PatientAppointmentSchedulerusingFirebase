package com.example.patientappointmentscheduler_usingfirebase.Adapter;


import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.ReservationInfoActivity;
import com.example.patientappointmentscheduler_usingfirebase.model.ReservationList;

public class ReservationListAdapter extends ListAdapter<ReservationList, ReservationListAdapter.ViewHolder> {

    private Activity mActivity;

    public ReservationListAdapter(Activity activity) {
        super(new ReservationDiffCallback());
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return
                new ViewHolder(inflater.inflate(R.layout.reservation_list_item, parent , false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
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

            lvReservationItem = itemView.findViewById(R.id.lvReservationItem);
            //on on click each item
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
                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }
        public void bindTo(ReservationList reservationList){
            tvCategoryName.setText(reservationList.getAppointmentCategory());
            tvDoctorsName.setText(reservationList.getDoctorsName());
            tvPatientsName.setText(reservationList.getPatientsName());
            tvReservationScheduleDateTime.setText(reservationList.getAppointmentDateTime());
            tvCreatedDate.setText(reservationList.getCurrentDate());
            tvReservationID.setText(reservationList.getReservationID());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }


    public static class ReservationDiffCallback extends DiffUtil.ItemCallback<ReservationList>{
        @Override
        public boolean areItemsTheSame(@NonNull ReservationList oldItem, @NonNull ReservationList newItem) {
            return
                oldItem.getReservationID().equals(newItem.getReservationID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReservationList oldItem, @NonNull ReservationList newItem) {
            return
                oldItem.getReservationID().equals(newItem.getReservationID()) &&
                oldItem.getLoggedInUid().equals(newItem.getLoggedInUid()) &&
                oldItem.getAppointmentCategory().equals(newItem.getAppointmentCategory()) &&
                oldItem.getPatientsName().equals(newItem.getPatientsName()) &&
                oldItem.getDoctorsName().equals(newItem.getDoctorsName()) &&
                oldItem.getAppointmentDateTime().equals(newItem.getAppointmentDateTime()) &&
                oldItem.getCurrentDate().equals(newItem.getCurrentDate()) &&
                oldItem.getStatus().equals(newItem.getStatus());
        }
    }
}
