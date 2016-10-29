package me.skywave.maternitycarelocker.locker.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by miri1 on 2016-10-14.
 */

public class InfoManager {

    private static InfoVO info = null;
    private Context context;

    public InfoManager(Context context) {
        this.context = context;
    }

    public InfoVO getInfoVO() {
        info.setName(getValue("profile_name"));
        info.setBirth(getValue("profile_birth"));
        info.setAllergies(getValue("profile_allergy"));
        info.setRecords(getValue("profile_record"));
        info.setMedicines(getValue("profile_medicine"));
        info.setComContact(new ContactVO(getValue("profile_com_contact")));
        info.setHptContact(new ContactVO(getValue("profile_hpt_contact")));
        info.setEtcContact(new ContactVO(getValue("profile_etc_contact")));
        info.setBlood(getValue("profile_blood"));
        return info;
    }

    private String getValue(String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, null);
    }


}
