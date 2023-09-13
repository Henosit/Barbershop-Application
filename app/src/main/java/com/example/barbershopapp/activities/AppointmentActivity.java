package com.example.barbershopapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.example.barbershopapp.R;
import com.example.barbershopapp.utils.ReadWriteAppointmentDetails;
import com.example.barbershopapp.utils.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AppointmentActivity extends Activity {

    private CalendarView calendarView;
    private Spinner barberSpinner;
    private Spinner treatmentSpinner;
    private Spinner timeSlotSpinner; // New Spinner for time slots
    private Button bookButton;

    private FirebaseAuth authProfile;
    FirebaseUser firebaseUser;

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

        // Set an onClickListener for the Book Button
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected date from the CalendarView
                long selectedDateInMillis = calendarView.getDate();

                // Convert the selected date to a readable format (you can customize this)
                String selectedDate = String.valueOf(selectedDateInMillis);

                // Get selected barber, treatment, and time slot from spinners
                String selectedBarber = barberSpinner.getSelectedItem().toString();
                String selectedTreatment = treatmentSpinner.getSelectedItem().toString();
                String selectedTimeSlot = timeSlotSpinner.getSelectedItem().toString();

                // Perform booking logic here (e.g., save to a database)

                bookAppointment(selectedDate,selectedBarber,selectedTreatment,selectedTimeSlot);
            }
        });
    }

    public void bookAppointment(String date, String barber, String treatment, String timeSlot) {
        // TODO: Perform availability check here (query your database or data source)
        if (isSlotAvailable(date, barber, treatment, timeSlot)) {
            // Enter Appointment Data into the Firebase Realtime DB
            ReadWriteAppointmentDetails writeAppointmentDetails = new ReadWriteAppointmentDetails(date, barber, treatment, timeSlot);

            // Extracting user reference from DB for "Appointments"
            DatabaseReference referenceAppointment = FirebaseDatabase.getInstance().getReference("Appointments");
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

    // Function to check if the selected date and time slot are available (you need to implement this)
    private boolean isSlotAvailable(String date, String barber, String treatment, String timeSlot) {
        // TODO: Implement logic to check availability from your data source
        // Return true if the slot is available, false otherwise.
        // You may need to query your database or perform other checks.
        return true;
    }
}