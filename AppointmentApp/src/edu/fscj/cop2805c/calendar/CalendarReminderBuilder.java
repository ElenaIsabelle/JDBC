// CalendarReminderBuilder.java
// D. Singletary
// 3/12/23
// New interface to represent a reminder builder
// (was originally included in CalenderReminder interface)

package edu.fscj.cop2805c.calendar;

public interface CalendarReminderBuilder {
    // build a reminder in the form of a formatted String
    public Reminder buildReminder(Appointment appt);
}
