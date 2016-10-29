package me.skywave.maternitycarelocker.companion.preference.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.MenuItem;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.companion.preference.SettingsActivity;

public class ProfilePreferenceFragment extends PreferenceFragment {
    static final int PICK_CONTACT = 1;
    private static final String[] PHONE_PROJECTION = new String[] {
            Phone.DISPLAY_NAME,
            Phone.NUMBER
    };
    Preference selectedContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_profile);

        Preference comContact = findPreference("profile_com_contact");
        comContact.setOnPreferenceClickListener(onContactClickListener);
        Preference hptContact = findPreference("profile_hpt_contact");
        hptContact.setOnPreferenceClickListener(onContactClickListener);
        Preference etcContact = findPreference("profile_etc_contact");
        etcContact.setOnPreferenceClickListener(onContactClickListener);

        SettingsActivity.bindPreferenceSummaryToValue(findPreference("profile_name"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("profile_birth"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("profile_record"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("profile_allergy"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("profile_medicine"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("profile_blood"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_CONTACT) {
                if (resultCode == Activity.RESULT_OK) {
                    Context context = getActivity().getApplicationContext();
                    Uri contactData = data.getData();
                    Cursor cursor = context.getContentResolver().query(contactData, PHONE_PROJECTION, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        selectedContact.setSummary(cursor.getString(0) + " " + cursor.getString(1));
                    }
                    cursor.close();
                }

        }
    }

    Preference.OnPreferenceClickListener onContactClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(i, PICK_CONTACT);
            selectedContact = preference;
            return true;
        }
    };
}
