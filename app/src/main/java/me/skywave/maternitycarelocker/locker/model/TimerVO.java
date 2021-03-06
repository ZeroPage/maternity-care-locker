package me.skywave.maternitycarelocker.locker.model;


public class TimerVO {
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
