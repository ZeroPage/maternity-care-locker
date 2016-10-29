package me.skywave.maternitycarelocker.locker.model;

/**
 * Created by miri1 on 2016-10-30.
 */

public class ContactVO {
    private String name;
    private String number;

    public ContactVO(String contact) {
        String[] splited = contact.split(" ");
        int len = splited.length;
        number = splited[len-1];
        name = "";
        for (int i = 0; i < len-1; i++) {
            name += splited[i] + " ";
        }
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
