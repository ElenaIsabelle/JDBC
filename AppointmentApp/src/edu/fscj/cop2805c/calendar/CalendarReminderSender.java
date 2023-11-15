// CalendarReminderSender.java
// D. Singletary
// 2/19/23
// Interface which supports building and sending reminders

// D. Singletary
// 3/12/23
// Split builder out and renamed interface

package edu.fscj.cop2805c.calendar;

public interface CalendarReminderSender {
    // send a reminder using contact's preferred notification method
    public void sendReminder(Reminder reminder);
}
