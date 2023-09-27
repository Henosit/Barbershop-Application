package com.example.barbershopapp.activities;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.example.barbershopapp.R;
import com.example.barbershopapp.utils.ReadWriteAppointmentDetails;
import com.example.barbershopapp.utils.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentActivity extends Activity {

    private CalendarView calendarView;
    private Spinner barberSpinner;
    private Spinner treatmentSpinner;
    private Spinner timeSlotSpinner; // New Spinner for time slots
    private Button bookButton;

    private FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        calendarView = findViewById(R.id.calendarView);
        barberSpinner = findViewById(R.id.barberSpinner);
        treatmentSpinner = findViewById(R.id.treatmentSpinner);
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner); // Initialize the new Spinner
        bookButton = findViewById(R.id.bookButton);

        // Set up the Barber Spinner
        ArrayAdapter<CharSequence> barberAdapter = ArrayAdapter.createFromResource(this, R.array.barbers_array, android.R.layout.simple_spinner_item);
        barberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barberSpinner.setAdapter(barberAdapter);

        // Set up the Treatment Spinner
        ArrayAdapter<CharSequence> treatmentAdapter = ArrayAdapter.createFromResource(this, R.array.treatments_array, android.R.layout.simple_spinner_item);
        treatmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        treatmentSpinner.setAdapter(treatmentAdapter);

        // Set up the Time Slot Spinner with available time slots
        ArrayAdapter<CharSequence> timeSlotAdapter = ArrayAdapter.createFromResource(this, R.array.time_slots_array, android.R.layout.simple_spinner_item);
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotSpinner.setAdapter(timeSlotAdapter);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                Date selectedDate = new Date(year - 1900, month, dayOfMonth);

                // Format the selected date as needed
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = sdf.format(selectedDate);
                currentDate = formattedDate;
            }
        });

        // Set an onClickListener for the Book Button
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDate != null) {
                    Toast.makeText(getApplicationContext(), "Selected Date: " + currentDate, Toast.LENGTH_SHORT).show();
                    // Get selected date from the CalendarView
                    String selectedBarber = barberSpinner.getSelectedItem().toString();
                    String selectedTreatment = treatmentSpinner.getSelectedItem().toString();
                    String selectedTimeSlot = timeSlotSpinner.getSelectedItem().toString();

                    // Perform booking logic here (e.g., save to a database)
                    if (selectedBarber=="") {
                        Toast.makeText(getApplicationContext(), "No barber selected yet", Toast.LENGTH_SHORT).show();
                    } else if (selectedTreatment=="") {
                        Toast.makeText(getApplicationContext(), "No treatment selected yet", Toast.LENGTH_SHORT).show();
                    } else if (selectedTimeSlot=="") {
                        Toast.makeText(getApplicationContext(), "No time slot selected yet", Toast.LENGTH_SHORT).show();
                    } else {
                        bookAppointment(currentDate, selectedBarber, selectedTreatment, selectedTimeSlot);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please click on the calendar to select your date", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void bookAppointment(String date, String barber, String treatment, String timeSlot) {
        // TODO: Perform availability check here (query your database or data source)
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Appointments");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isSlotAvailable = true; // Assume the slot is initially available

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String existingDate = snapshot.child("date").getValue(String.class);
                    String existingBarber = snapshot.child("barber").getValue(String.class);
                    String existingTimeSlot = snapshot.child("timeSlot").getValue(String.class);

                    // Check if the date, barber, and time slot match an existing appointment
                    try {
                        if (existingDate.equals(date) && existingBarber.equals(barber) && existingTimeSlot.equals(timeSlot)) {
                            // Slot is not available since there's a conflicting appointment
                            isSlotAvailable = false;
                            break; // No need to check further
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "There is a problem in scheduling the appointment, please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (isSlotAvailable) {
                    // Enter Appointment Data into the Firebase Realtime DB
                    ReadWriteAppointmentDetails writeAppointmentDetails = new ReadWriteAppointmentDetails(date, barber, treatment, timeSlot);

                    // Extracting user reference from DB for "Appointments"
                    DatabaseReference referenceAppointment = FirebaseDatabase.getInstance().getReference("Appointments");
                    // Note: a user can only schedule one appointment
                    referenceAppointment.child(firebaseUser.getUid()).setValue(writeAppointmentDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Display a confirmation message
                                Toast.makeText(getApplicationContext(), "Appointment booked for " +
                                                date + "\nBarber: " + barber + "\nTreatment: " + treatment + "\nTime Slot: " + timeSlot,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Appointment booking failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Show a message to the user indicating that the selected slot is already booked
                    Toast.makeText(getApplicationContext(), "This time slot is already booked. Please choose another time slot.", Toast.LENGTH_LONG).show();
                }
            }

            public void onCancelled(DatabaseError databaseError) {
                // Handle database read error, if needed
            }
        });
    }
}