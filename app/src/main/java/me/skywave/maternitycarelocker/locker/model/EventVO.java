package me.skywave.maternitycarelocker.locker.model;

public class EventVO {
    private long dDay;
    private String title;

    public EventVO(String title, long dDay) {
        this.dDay = dDay;
        this.title = title;
    }

    public long getDDay() {
        return dDay;
    }

    public String getTitle() {
        return title;
    }
}
