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
        if (info == null) {
            info = new InfoVO();
        }
        String value;

        value = getValue("profile_name");
        info.setName((value == null) ? "":value);
        value = getValue("profile_birth");
        info.setBirth((value == null) ? "":value);
        value = getValue("profile_allergy");
        info.setAllergies((value == null) ? "":value);
        value = getValue("profile_record");
        info.setRecords((value == null) ? "":value);
        value = getValue("profile_medicine");
        info.setMedicines((value == null) ? "":value);
        value = getValue("profile_com_contact");
        info.setComContact((value == null)? new ContactVO():new ContactVO(value));
        value = getValue("profile_hpt_contact");
        info.setHptContact((value == null)? new ContactVO():new ContactVO(value));
        value = getValue("profile_etc_contact");
        info.setEtcContact((value == null)? new ContactVO():new ContactVO(value));
        value = getValue("profile_blood_contact");
        info.setBlood((value == null) ? "":value);

        return info;
    }

    private String getValue(String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, null);
    }


}
