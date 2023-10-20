package com.example.barbershopapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.barbershopapp.R;
import com.example.barbershopapp.utils.AppointmentAdapter;
import com.example.barbershopapp.utils.AppointmentComparator;
import com.example.barbershopapp.utils.ReadWriteAppointmentDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewAppointmentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments);
        recyclerView = findViewById(R.id.appointment_recycle_view);
        List<ReadWriteAppointmentDetails> appointmentList = new ArrayList<>(); // Populate this list with appointment data.
        AppointmentAdapter adapter = new AppointmentAdapter(appointmentList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getAppointmentData();
    }
    private void getAppointmentData() {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("Appointments");

        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ReadWriteAppointmentDetails> appointmentList = new ArrayList<>();

                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                    // Extract the date from each appointment
                    String date = appointmentSnapshot.child("date").getValue(String.class);
                    String barber = appointmentSnapshot.child("barber").getValue(String.class);
                    String treatment = appointmentSnapshot.child("treatment").getValue(String.class);
                    String timeSlot = appointmentSnapshot.child("timeSlot").getValue(String.class);

                    // Create an Appointment object and add it to the list
                    ReadWriteAppointmentDetails appointment = new ReadWriteAppointmentDetails(date,barber,treatment,timeSlot);
                    appointmentList.add(appointment);
                }
                // Sort the list of appointments by date
                Collections.sort(appointmentList, new AppointmentComparator());

                // Update the RecyclerView adapter with the fetched data
                adapter = new AppointmentAdapter(appointmentList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if any
            }
        });
    }

}