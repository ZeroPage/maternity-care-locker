package me.skywave.maternitycarelocker.locker.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by Jeong on 2016. 10. 16..
 */

public class TimerVO extends RealmObject {
    @Ignore
    public static final String TYPE_PAIN = "진통";
    public static final String TYPE_REST = "휴식";

    private String type;
    private String start;
    private String period;

    public TimerVO() {
    }

    public TimerVO(String type, String start, String period) {
        this.type = type;
        this.start = start;
        this.period = period;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
