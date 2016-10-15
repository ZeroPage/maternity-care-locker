package me.skywave.maternitycarelocker.locker.model;

/**
 * Created by miri1 on 2016-10-14.
 */

public class InfoManager {

    private static InfoVO info = null;

    public static InfoVO getInfoVO() {
        if (info == null) {
            info = new InfoVO();
            info.setName("임 산부");
            info.setBirth("1993.12.08");
            info.setAllergies("고양이 알레르기, 계란 알레르기, 땅콩 알레르기");
            info.setMedicines("혈압약");
            info.addContactName("박희정");
            info.addContact("01047429665");
            info.addContactName("조영준");
            info.addContact("01096586067");
            info.setBlood("A Rh+");
        }
        return info;
    }
}
