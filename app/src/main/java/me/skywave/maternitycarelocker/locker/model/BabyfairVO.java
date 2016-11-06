package me.skywave.maternitycarelocker.locker.model;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class BabyfairVO {
    private long dateFrom;
    private long dateTo;
    private String description;
    private String detailUrl;
    private String location;
    private String title;

    public BabyfairVO() {

    }

    public long getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(long dateFrom) {
        this.dateFrom = dateFrom;
    }

    public long getDateTo() {
        return dateTo;
    }

    public void setDateTo(long dateTo) {
        this.dateTo = dateTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDDay() {
        long currentDay = TimeUnit.DAYS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        long fairDay = TimeUnit.DAYS.convert(dateFrom, TimeUnit.MILLISECONDS);

        return fairDay - currentDay;
    }
}
