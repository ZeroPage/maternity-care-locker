package me.skywave.maternitycarelocker.locker;

import java.util.Calendar;

public class EventVO {
    private Calendar date;
    private String title;

    public EventVO(String title, Calendar date) {
        this.date = date;
        this.title = title;
    }

    public Calendar getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }
}
