package me.skywave.maternitycarelocker.locker.model;

/**
 * Created by Jeong on 2016. 10. 16..
 */

public class TimerVO {
    public static final String TYPE_PAIN = "진통";
    public static final String TYPE_REST = "휴식";
    private String type;
    private String start;
    private String preiod;

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

    public String getPreiod() {
        return preiod;
    }

    public void setPreiod(String preiod) {
        this.preiod = preiod;
    }
}
