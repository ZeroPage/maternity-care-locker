package me.skywave.maternitycarelocker.locker.model;

/**
 * Created by miri1 on 2016-10-14.
 */

public class InfoVO {


    private String name;
    private String birth;
    private String records;
    private String allergies;
    private String medicines;
    private ContactVO comContact;
    private ContactVO hptContact;
    private ContactVO etcContact;
    private String blood;

    public InfoVO() {
        name = null;
        birth = null;
        records = null;
        allergies = null;
        medicines = null;
        comContact = null;
        hptContact = null;
        etcContact = null;
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

    public ContactVO getComContact() {
        return comContact;
    }

    public void setComContact(ContactVO comContact) {
        this.comContact = comContact;
    }

    public ContactVO getHptContact() {
        return hptContact;
    }

    public void setHptContact(ContactVO hptContact) {
        this.hptContact = hptContact;
    }

    public ContactVO getEtcContact() {
        return etcContact;
    }

    public void setEtcContact(ContactVO etcContact) {
        this.etcContact = etcContact;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }
}
