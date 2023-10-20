package com.example.barbershopapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadWriteAppointmentDetails {

    private String date, barber, treatment, timeSlot;

    public ReadWriteAppointmentDetails() {
    }

    public ReadWriteAppointmentDetails(String date, String barber, String treatment, String timeSlot) {
        this.date = date;
        this.barber = barber;
        this.treatment = treatment;
        this.timeSlot = timeSlot;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBarber() {
        return barber;
    }

    public void setBarber(String barber) {
        this.barber = barber;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getAppointmentDetails() {
        String appointmentMessage = "Your appointment is scheduled for:\n"
                + getDate() + ", "
                + getBarber() + ", "
                + getTreatment() + ", "
                + getTimeSlot() + ".\n"
                + "We look forward to seeing you!";
        return appointmentMessage;
    }
}
