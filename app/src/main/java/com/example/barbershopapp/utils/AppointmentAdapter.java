package com.example.barbershopapp.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barbershopapp.R;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private List<ReadWriteAppointmentDetails> appointments;
    private FirebaseAuth authProfile;

    public AppointmentAdapter(List<ReadWriteAppointmentDetails> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReadWriteAppointmentDetails appointment = appointments.get(position);
        holder.textViewDate.setText("Date: " + appointment.getDate());
        holder.textViewBarber.setText("Barber: " + appointment.getBarber());
        holder.textViewTreatment.setText("Treatment: " + appointment.getTreatment());
        holder.textViewTimeSlot.setText("TimeSlot: " + appointment.getTimeSlot());
        holder.textViewUserName.setText("UserName: ");
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;
        TextView textViewBarber;
        TextView textViewTimeSlot;
        TextView textViewTreatment;
        TextView textViewUserName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTimeSlot=itemView.findViewById(R.id.textViewTimeSlot);
            textViewTreatment = itemView.findViewById(R.id.textViewTreatment);
            textViewBarber=itemView.findViewById(R.id.textViewBarber);
            textViewUserName=itemView.findViewById(R.id.textViewAppointment_userName);
        }
    }
}
