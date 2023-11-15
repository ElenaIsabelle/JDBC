// Appointment.java
// D. Singletary
// 2/19/23
// Class which represents an appointment

package edu.fscj.cop2805c.calendar;

import java.time.ZonedDateTime;

// class which represents appointments
public class Appointment {

    private String title;
    private String description;
    private Contact contact;
    private ZonedDateTime apptTime;
    private ZonedDateTime reminder;

    public Appointment(String title, String description, Contact contact,
                       ZonedDateTime apptTime) {
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.apptTime = apptTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Contact getContact() {
        return contact;
    }

    public ZonedDateTime getApptTime() {
        return apptTime;
    }

    public ZonedDateTime getReminder() {
        return reminder;
    }

    public void setReminder(ZonedDateTime reminder) {
        this.reminder = reminder;
    }

    @Override
    public String toString() {
        String s = "Appt:\n" +
                "\tTitle: " + this.getTitle() + "\n" +
                "\tDesc: " + this.getDescription() + "\n" +
                "\tContact: " + this.getContact() + "\n" +
                "\tAppt Date/Time: " + this.getApptTime() + "\n" +
                "\tReminder: " + this.getReminder() + "\n";
        return s;
    }
}
