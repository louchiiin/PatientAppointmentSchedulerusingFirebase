package com.example.patientappointmentscheduler_usingfirebase.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patientappointmentscheduler_usingfirebase.R;
import com.example.patientappointmentscheduler_usingfirebase.Activity.ReservationInfoActivity;
import com.example.patientappointmentscheduler_usingfirebase.model.ReservationList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReservationListAdapter extends ListAdapter<ReservationList, ReservationListAdapter.ViewHolder> {

    private Activity mActivity;
    private Context mContext;

    public ReservationListAdapter(Activity activity, Context context) {
        super(new ReservationDiffCallback());
        this.mActivity = activity;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.reservation_list_item, parent , false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private final TextView tvCategoryName, tvDoctorsName, tvPatientsName, tvReservationScheduleDateTime, tvCreatedDate, tvReservationID;
        private final LinearLayout lvReservationItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDoctorsName = itemView.findViewById(R.id.tvDoctorsName);
            tvPatientsName = itemView.findViewById(R.id.tvPatientsName);
            tvReservationScheduleDateTime = itemView.findViewById(R.id.tvReservationScheduleDateTime);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            tvReservationID = itemView.findViewById(R.id.tvReservationID);

            lvReservationItem = itemView.findViewById(R.id.list_reservation_item);
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

            String getDateTime = reservationList.getAppointmentDateTime();

            DateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
            Date currentDateTime = new Date();
            String newDate = inputFormat.format(currentDateTime);

            Date currentDateFormatted = null;
            Date reservationTime = null;
            try {
                currentDateFormatted = inputFormat.parse(newDate);
                reservationTime = inputFormat.parse(getDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //reservation time is less than current Date
            assert reservationTime != null;
            if (reservationTime.compareTo(currentDateFormatted) < 0)    {
                lvReservationItem.setClickable(false);
                lvReservationItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dark_gray));
            } else {
                lvReservationItem.setClickable(true);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ReservationDiffCallback extends DiffUtil.ItemCallback<ReservationList>{
        @Override
        public boolean areItemsTheSame(@NonNull ReservationList oldItem, @NonNull ReservationList newItem) {
            return oldItem.getReservationID().equals(newItem.getReservationID());
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
