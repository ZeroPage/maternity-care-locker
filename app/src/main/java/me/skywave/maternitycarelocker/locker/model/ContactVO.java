package me.skywave.maternitycarelocker.locker.model;

/**
 * Created by miri1 on 2016-10-30.
 */

public class ContactVO {
    private String name;
    private String number;

    public ContactVO(String contact) {
        if (contact != null) {
            String[] splited = contact.split(" ");
            int len = splited.length;
            number = splited[len - 1];
            name = "";
            for (int i = 0; i < len - 1; i++) {
                name += splited[i] + " ";
            }
        }
        else {
            name = "";
            number = "";
        }
    }

    public ContactVO() {
        name = "";
        number = "";
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public boolean isEmpty() {
        return name.isEmpty();
    }
}
