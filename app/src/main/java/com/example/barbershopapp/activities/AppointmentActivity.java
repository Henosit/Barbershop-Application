package com.example.barbershopapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.barbershopapp.R;

public class AppointmentActivity extends Activity {

    private CalendarView calendarView;
    private Spinner barberSpinner;
    private Spinner treatmentSpinner;
    private Spinner timeSlotSpinner; // New Spinner for time slots
    private Button bookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

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

                // Display a confirmation message
                Toast.makeText(getApplicationContext(), "Appointment booked for " +
                                selectedDate + "\nBarber: " + selectedBarber + "\nTreatment: " + selectedTreatment + "\nTime Slot: " + selectedTimeSlot,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}