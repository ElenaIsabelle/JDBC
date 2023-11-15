// Contact.java
// D. Singletary
// 2/19/23
// Class which represents a contact

package edu.fscj.cop2805c.calendar;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Locale;

// class which represents the appointment contact
public class Contact implements Serializable {
    private static Integer idStatic = 0;
    private Integer id; // primary key
    private String fName;
    private String lName;
    private String email;
    private String phone;
    private ReminderPreference remindPref;
    private ZoneId zoneId;
    private Locale locale;

    public Contact(Integer id, String lName, String fName,
                   String email, String phone,
                   ReminderPreference remindPref,
                   ZoneId zoneId, Locale locale) {
        if (id == 0) {
            idStatic++;
            this.id = idStatic;
        } else {
            this.id = id;
        }
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phone = phone;
        this.remindPref = remindPref;
        this.zoneId = zoneId;
        this.locale = locale;
    }

    public Integer getId() { return id; }

    public String getLName() {
        return lName;
    }

    public String getFName() {
        return fName;
    }

    // build firstName lastName string
    public String getName() {
        return fName + " " + lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ReminderPreference getRemindPref() {
        return remindPref;
    }

    public void setRemindPref(ReminderPreference remindPref) {
        this.remindPref = remindPref;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setTimeZoneOffset(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public Locale getLocale() { return locale; }

    @Override
    public String toString() {
        String s = this.getEmail() + "," +
                "(" + this.getLName() + "," + this.getFName() + ")" +
                this.getPhone() + "," +
                this.getRemindPref() + "," +
                this.getZoneId();
        return s;
    }
}
