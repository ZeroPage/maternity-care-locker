package me.skywave.maternitycarelocker.locker.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Jeong on 2016. 10. 30..
 */

public class TimerSetVO extends RealmObject {
    private String date;
    private RealmList<TimerVO> timerVos;

    public TimerSetVO() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RealmList<TimerVO> getTimerVos() {
        return timerVos;
    }

    public void setTimerVos(RealmList<TimerVO> timerVos) {
        this.timerVos = timerVos;
    }
}
