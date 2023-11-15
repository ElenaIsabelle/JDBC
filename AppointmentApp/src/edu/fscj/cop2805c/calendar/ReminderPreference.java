package edu.fscj.cop2805c.calendar;

public enum ReminderPreference {
    NONE, EMAIL, PHONE;

    public static ReminderPreference parse(String prefStr) {
        ReminderPreference pref = NONE;
        switch (prefStr) {
            case "EMAIL":
                pref = EMAIL;
                break;
            case "PHONE":
                pref = PHONE;
                break;
        }
        return pref;
    }
}
