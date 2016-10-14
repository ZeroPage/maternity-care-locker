package me.skywave.maternitycarelocker.locker.model;

/**
 * Created by miri1 on 2016-10-14.
 */

public class InfoManager {

    private static InfoVO info = null;

    public static InfoVO getInfoVO() {
        if (info == null) {
            info = new InfoVO();
            info.setName("안미리");
            info.setBirth("1993.12.08");
            info.setAllergies("과제 알레르기, 졸업논문 알레르기, 자소서 알레르기, 면접 알레르기");
            info.setMedicines("감기약");
            info.addContactName("박희정");
            info.addContact("01047429665");
            info.addContactName("조영준");
            info.addContact("01096586067");
            info.addContactName("엄마");
            info.addContact("01071957495");
            info.setBlood("A Rh+");
        }
        return info;
    }
}
