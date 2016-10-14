package me.skywave.maternitycarelocker.locker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miri1 on 2016-10-14.
 */

public class InfoVO {

    private String name;
    private String birth;
    private String records;
    private String allergies;
    private String medicines;
    private List<String> contactName;
    private List<String> contactNum;
    private String blood;

    public InfoVO() {
        name = null;
        birth = null;
        records = null;
        allergies = null;
        medicines = null;
        contactName = new ArrayList<>();
        contactNum = new ArrayList<>();
        blood = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }


    public String getRecords() {
        return records;
    }

    public void setRecords(String records) {
        this.records = records;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public List<String> getContactName() {
        return contactName;
    }

    public void addContactName(String name) {
        contactName.add(name);
    }

    public List<String> getContactNum() {
        return contactNum;
    }

    public void addContact(String number) {
        contactNum.add(number);
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }
}
