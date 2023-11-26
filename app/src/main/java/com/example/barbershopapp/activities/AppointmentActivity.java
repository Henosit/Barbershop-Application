package com.example.barbershopapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.example.barbershopapp.R;
import com.example.barbershopapp.fragments.FragmentUserProfile;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AppointmentActivity extends Activity {

    private CalendarView calendarView;
    private Spinner barberSpinner;
    private Spinner treatmentSpinner;
    private Spinner timeSlotSpinner; // New Spinner for time slots
    private Button bookButton;

    private Button cancelBookButton;
    private FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    String currentDate;

    private boolean appointmentExists;

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
        cancelBookButton=findViewById(R.id.cancelBookButton);
        checkIfAppointmentExists();
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
        cancelBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAppointment();
                if(!appointmentExists)
                    cancelBookButton.setVisibility(View.GONE);

            }
        });
        // Set an onClickListener for the Book Button
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDate != null) {
                    Toast.makeText(getApplicationContext(), "Selected Date: " + currentDate, Toast.LENGTH_SHORT).show();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date today = new Date();
                    String todayDate = dateFormat.format(today);
                    if (currentDate.compareTo(todayDate)<0) {
                        Toast.makeText(getApplicationContext(), "Please select a valid, future date", Toast.LENGTH_SHORT).show();
                    } else {
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
                            // Update time slot colors when a date is selected
                            TimeZone israelTimeZone = TimeZone.getTimeZone("Asia/Jerusalem");
                            Calendar calendar = Calendar.getInstance(israelTimeZone);
                            int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // Get the current hour
                            int selectedHour = Integer.parseInt(selectedTimeSlot.split(":")[0]);
                            String amPm = selectedTimeSlot.split(" ")[1]; // AM or PM
                            // Adjust the selected hour based on AM/PM
                            if (amPm.equals("PM") && selectedHour != 12) {
                                selectedHour += 12;
                            } else if (amPm.equals("AM") && selectedHour == 12) {
                                selectedHour = 0;
                            }

                            if (currentDate.equals(todayDate)) {
                                if (selectedHour < currentHour) {
                                    Toast.makeText(getApplicationContext(), "Please select a valid, future date", Toast.LENGTH_SHORT).show();
                                } else {
                                    bookAppointment(currentDate, selectedBarber, selectedTreatment, selectedTimeSlot);
                                }
                            } else {
                                bookAppointment(currentDate, selectedBarber, selectedTreatment, selectedTimeSlot);
                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please click on the calendar to select your date", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        timeSlotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), "Item Selected", Toast.LENGTH_SHORT).show();
//                updateSlotColors(currentDate);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(getApplicationContext(), "None Selected", Toast.LENGTH_SHORT).show();
//            }
//        });
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
                                appointmentExists=true;
//                                Toast.makeText(getApplicationContext(),"If you wish to change your appointment, please restart the application.",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(AppointmentActivity.this, MainActivity.class);
//                                getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//clear stack
                                startActivity(intent);
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
    public void cancelAppointment() {
        // Get a reference to your Firebase Realtime Database
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("Appointments");

        // Get the appointment details from the selectedAppointment


        // Get the appointment key to remove the appointment
        Query appointmentKey = appointmentsRef.orderByKey().equalTo(firebaseUser.getUid());

        // Remove the appointment from the database
        appointmentKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the key of the data you want to delete
                    String key = snapshot.getKey();
                    String existingDate = snapshot.child("date").getValue(String.class);
                    String existingBarber = snapshot.child("barber").getValue(String.class);
                    String existingTimeSlot = snapshot.child("timeSlot").getValue(String.class);
                    String existingTreatment = snapshot.child("timeSlot").getValue(String.class);

                    // Remove the data
                    appointmentsRef.child(key).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Display a confirmation message
                                        Toast.makeText(getApplicationContext(), "Appointment Canceled Successfully for " +
                                                        existingTimeSlot + "\nBarber: " + existingBarber + "\nTreatment: " + existingTreatment + "\nTime Slot: " + existingTimeSlot,
                                                Toast.LENGTH_LONG).show();
                                        appointmentExists = false;
                                        Intent intent=new Intent(AppointmentActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Appointment cancellation failed. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AppointmentActivity.this, error.getMessage() + " Please try later again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfAppointmentExists() {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("Appointments");

        Query userAppointmentQuery = appointmentsRef.orderByKey().equalTo(firebaseUser.getUid());

        userAppointmentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // An appointment exists for the current user, so make the button visible
                    cancelBookButton.setVisibility(View.VISIBLE);
                    appointmentExists = true;
                } else {
                    // No appointment exists, so hide the button
                    cancelBookButton.setVisibility(View.GONE);
                    appointmentExists = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AppointmentActivity.this, databaseError.getMessage() + " Please try later again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Method to update time slot colors based on availability
//    private void updateSlotColors(String selectedDate) {
//        if (selectedDate != null) {
//            ArrayAdapter<CharSequence> timeSlotAdapter = ArrayAdapter.createFromResource(this, R.array.time_slots_array, android.R.layout.simple_spinner_item);
//
//            // Create ColorStateList for available and unavailable time slots
//            int darkTurquoiseColor = ContextCompat.getColor(this, R.color.dark_turquoise); // Define your color resource
//            int whiteColor = Color.WHITE; // Change this to your desired color
//            ColorStateList colorStateList;
//
//            // TODO: Implement the logic to retrieve unavailable time slots for the selectedDate
//            List<String> unavailableTimeSlots = getUnavailableTimeSlots(selectedDate);
//
//            // Iterate through time slots and update their colors
//            for (int i = 0; i < timeSlotAdapter.getCount(); i++) {
//                String timeSlot = timeSlotAdapter.getItem(i).toString();
//
//                // Check if the time slot is in the list of unavailable time slots
//                boolean isSlotUnavailable = unavailableTimeSlots.contains(timeSlot);
//
//                // Set the appropriate color for the time slot
//                if (isSlotUnavailable) {
//                    colorStateList = ColorStateList.valueOf(darkTurquoiseColor); // Unavailable slot color
//                } else {
//                    colorStateList = ColorStateList.valueOf(whiteColor); // Available slot color
//                }
//
//                // Get the selected item view and set its background color
//                View selectedView = timeSlotSpinner.getSelectedView();
//                if (selectedView != null) {
//                    selectedView.setBackgroundTintList(colorStateList);
//                }
//            }
//        }
//    }

//    private List<String> getUnavailableTimeSlots(String selectedDate) {
//        // Initialize the list of unavailable time slots
//        List<String> unavailableTimeSlots = new ArrayList<>();
//
//        // Get a reference to your Firebase database
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Appointments");
//
//        // Construct a query to find appointments for the selected date
//        Query query = databaseReference.orderByChild("date").equalTo(selectedDate);
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Iterate through the appointments for the selected date
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String existingTimeSlot = snapshot.child("timeSlot").getValue(String.class);
//
//                    // Add the unavailable time slot to the list
//                    unavailableTimeSlots.add(existingTimeSlot);
//                }
//
//                // Now, you have a list of unavailable time slots for the selected date
//                // You can use this list to determine which time slots are unavailable
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle any errors in reading the database, if needed
//            }
//        });
//        if (unavailableTimeSlots!=null) {
//            for (int i=0;i<unavailableTimeSlots.size();i++) {
//                Toast.makeText(getApplicationContext(), unavailableTimeSlots.get(i), Toast.LENGTH_SHORT).show();
//            }
//        }
//        return unavailableTimeSlots;
//    }
}