// AppointmentApp.java
// D. Singletary
// 1/23/22
// creates an appointment for a contact

// D. Singletary
// 2/19/23
// Added Stream and localization code

// D. Singletary
// 3/12/23
// Removed buildReminder to Reminder class, that class now
// implements CalendarReminderBuilder
// Changed this class to only implement CalendarReminderSender
// Changed to thread-safe queue
// Instantiate the ReminderProcessor object
// added test data for multi-threading tests

package edu.fscj.cop2805c.calendar;

import edu.fscj.cop2805c.dispatch.Dispatcher;

import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.stream.Stream;

// main application class
public class AppointmentApp implements CalendarReminderSender, Dispatcher<Reminder> {

    private ArrayList<Appointment> appointments = new ArrayList<>();
    private static final String CONTACT_FILE = "contact.dat";

    // Use a thread-safe Queue<LinkedList> to act as message queue for the dispatcher
    ConcurrentLinkedQueue safeQueue = new ConcurrentLinkedQueue(
            new LinkedList<Reminder>()
    );

    private Random rand = new Random();
    private int numAppointments = 0;

    // dispatch the reminder using the dispatcher
    public void dispatch(Reminder reminder) {
        this.safeQueue.add(reminder);
    }

    // send a reminder using contact's preferred notification method
    public void sendReminder(Reminder reminder) {
        Contact c = reminder.getContact();
        if (c.getRemindPref() == ReminderPreference.NONE)
            System.out.println(
                    "Error: no Reminder Preference set for " + c.getName());
        else {
            dispatch(reminder);
        }

//        Dispatcher<Reminder> d = (r)-> {
//            this.queue.add(r);
//            // System.out.println("current queue length is " + this.queue.size());  // debug
//        };
//        d.dispatch(reminder);
    }

    private Appointment createRandomAppointment(Contact c) {
        ZonedDateTime apptTime, reminder;
        int plusVal = rand.nextInt() % 12 + 1;
        // create a future appointment using random month value
        apptTime = ZonedDateTime.now().plusMonths(plusVal);

        // create the appt reminder for the appointment time minus random (<24) hours
        // use absolute value in case random is negative to prevent reminders > appt
        int minusVal = Math.abs(rand.nextInt()) % 24 + 1;
        reminder = apptTime.minusHours(minusVal);
        // create an appointment using the contact and appt time
        Appointment appt = new Appointment("Test Appointment " + ++numAppointments,
                "This is test appointment " + numAppointments,
                c, apptTime);
        appt.setReminder(reminder);
        return appt;
    }

    private void addAppointments(Appointment... appts) {
        for (Appointment a : appts) {
            if (!appointments.contains(a))
                appointments.add(a);
            else
                System.out.println("duplicate - not added");
        }
    }

    private void checkReminderTime(Appointment appt) {
        ZonedDateTime current = ZonedDateTime.now();
        ZonedDateTime dt = appt.getReminder();

        // see if it's time to send a reminder
        // TODO: create a Reminder class and override equals()
        if (    dt.getYear() == current.getYear() &&
                dt.getMonth() == current.getMonth() &&
                dt.getDayOfMonth() == current.getDayOfMonth() &&
                dt.getHour() == current.getHour() &&
                dt.getMinute() == current.getMinute()) {
            Reminder reminder = new Reminder().buildReminder(appt);
            sendReminder(reminder);
        }
    }

    // write contact ArrayList to save file
    public void writeContacts(ArrayList<Contact> cl) {
        try (ObjectOutputStream contactData =  new ObjectOutputStream(
                new FileOutputStream(CONTACT_FILE));) {
            contactData.writeObject(cl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read saved user ContactList
    public ArrayList<Contact> readContacts() {
        ArrayList<Contact> list = new ArrayList();

        try (ObjectInputStream contactData =
                     new ObjectInputStream(
                             new FileInputStream(CONTACT_FILE));) {
            list = (ArrayList<Contact>) (contactData.readObject());
            for (Contact c : list)
                System.out.println("readContacts: read " + c);
        } catch (FileNotFoundException e) {
            // not  a problem if nothing was saved
            System.err.println("readContacts: no input file");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    // unit test
    public static void main(String[] args) {
        AppointmentApp apptApp = new AppointmentApp();

        ZonedDateTime current = ZonedDateTime.now();

        ContactDB.createDB();
        ArrayList<Contact> contactList = ContactDB.readContactDB();

        ReminderProcessor processor = new ReminderProcessor(apptApp.safeQueue);

        // create some appointments, one for each contact
        for (Contact c : contactList) {
            Appointment a = apptApp.createRandomAppointment(c);
            a.setReminder(current);
            apptApp.addAppointments(a);
        }

        // send reminders where needed
        for (Appointment a : apptApp.appointments)
            apptApp.checkReminderTime(a);

        // wait for a bit
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            System.out.println("sleep interrupted! " + ie);
        }

        // reset reminder times and do it again
        current = ZonedDateTime.now();
        for (Contact c : contactList) {
            Appointment a = apptApp.createRandomAppointment(c);
            a.setReminder(current);
            apptApp.addAppointments(a);
        }

        for (Appointment a : apptApp.appointments)
            apptApp.checkReminderTime(a);

        // wait for a bit before exiting so queue can process
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            System.out.println("sleep interrupted! " + ie);
        }

        // stop processing
        processor.endProcessing();
        ContactDB.deleteDB();
    }
}
