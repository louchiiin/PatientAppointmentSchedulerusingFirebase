package com.example.patientappointmentscheduler_usingfirebase.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.ReservationInfoActivity;
import com.example.patientappointmentscheduler_usingfirebase.model.ReservationList;

public class ReservationListAdapter extends ListAdapter<ReservationList, ReservationListAdapter.ViewHolder> {

    public ReservationListAdapter() {
        super(new ReservationDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return
                new ViewHolder(inflater.inflate(R.layout.reservation_list_item, parent , false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView tvCategoryName, tvDoctorsName, tvPatientsName, tvReservationScheduleDate, tvReservationScheduleTime, tvCreatedDate;
        LinearLayout lvReservationItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDoctorsName = itemView.findViewById(R.id.tvDoctorsName);
            tvPatientsName = itemView.findViewById(R.id.tvPatientsName);
            tvReservationScheduleDate = itemView.findViewById(R.id.tvReservationScheduleDate);
            tvReservationScheduleTime = itemView.findViewById(R.id.tvReservationScheduleTime);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);

            lvReservationItem = itemView.findViewById(R.id.lvReservationItem);
            //on on click each item
            lvReservationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //values to string
                    String categoryName = tvCategoryName.getText().toString();
                    String doctorsName = tvDoctorsName.getText().toString();
                    String patientsName = tvPatientsName.getText().toString();
                    String reservationScheduleDate = tvReservationScheduleDate.getText().toString();
                    String reservationScheduleTime = tvReservationScheduleTime.getText().toString();
                    String createdDate = tvCreatedDate.getText().toString();

                    Intent intent = new Intent(view.getContext(), ReservationInfoActivity.class);
                    intent.putExtra("CATEGORY NAME", categoryName);
                    intent.putExtra("DOCTORS NAME", doctorsName);
                    intent.putExtra("PATIENTS NAME", patientsName);
                    intent.putExtra("SCHEDULE DATE", reservationScheduleDate);
                    intent.putExtra("SCHEDULE TIME", reservationScheduleTime);
                    intent.putExtra("CREATED DATE", createdDate);
                    view.getContext().startActivity(intent);
                }
            });
        }
        public void bindTo(ReservationList reservationList){
            tvCategoryName.setText(reservationList.getAppointmentCategory());
            tvDoctorsName.setText(reservationList.getDoctorsName());
            tvPatientsName.setText(reservationList.getPatientsName());
            tvReservationScheduleDate.setText(reservationList.getAppointmentDate());
            tvReservationScheduleTime.setText(reservationList.getAppointmentTime());
            tvCreatedDate.setText(reservationList.getCurrentDate());
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
                oldItem.getAppointmentDate().equals(newItem.getAppointmentDate()) &&
                oldItem.getAppointmentTime().equals(newItem.getAppointmentTime()) &&
                oldItem.getCurrentDate().equals(newItem.getCurrentDate()) &&
                oldItem.getStatus().equals(newItem.getStatus());
        }
    }
}
