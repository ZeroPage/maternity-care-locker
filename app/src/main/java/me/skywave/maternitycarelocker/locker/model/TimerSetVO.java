package me.skywave.maternitycarelocker.locker.model;


import java.util.List;

public class TimerSetVO {
    private String date;
    private List<TimerVO> timers;

    public TimerSetVO() {

    }

    public TimerSetVO(String date, List<TimerVO> timers) {
        this.date = date;
        this.timers = timers;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<TimerVO> getTimerVos() {
        return timers;
    }

    public void setTimerVos(List<TimerVO> timerVos) {
        this.timers = timerVos;
    }
}
