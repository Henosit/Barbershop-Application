package com.example.barbershopapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Comparator;

public class AppointmentComparator implements Comparator<ReadWriteAppointmentDetails> {
    @Override
    public int compare(ReadWriteAppointmentDetails appointment1, ReadWriteAppointmentDetails appointment2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        try {
            Date date1 = dateFormat.parse(appointment1.getDate());
            Date date2 = dateFormat.parse(appointment2.getDate());

            int dateComparison = date1.compareTo(date2);

            if (dateComparison == 0) {
                // If the dates are the same, compare the time slots
                Date time1 = timeFormat.parse(appointment1.getTimeSlot());
                Date time2 = timeFormat.parse(appointment2.getTimeSlot());
                return time1.compareTo(time2);
            }

            return dateComparison;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0; // Handle errors as needed
    }
}
