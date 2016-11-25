package me.skywave.maternitycarelocker.companion.preference.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.MenuItem;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.companion.CompanionActivity;
import me.skywave.maternitycarelocker.companion.preference.SettingsActivity;
import me.skywave.maternitycarelocker.utils.FirebaseHelper;

public class ProfilePreferenceFragment extends PreferenceFragment {
    static final int PICK_CONTACT = 1;
    private static final String[] PHONE_PROJECTION = new String[] {
            Phone.DISPLAY_NAME,
            Phone.NUMBER
    };
    private Preference selectedContact;
    private SharedPreferences.Editor editor;


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
        SettingsActivity.bindPreferenceSummaryToValue(comContact);
        SettingsActivity.bindPreferenceSummaryToValue(hptContact);
        SettingsActivity.bindPreferenceSummaryToValue(etcContact);

        final DatePreference pregnancyDatePreference = (DatePreference) findPreference("profile_pregnancy_date");
        final ListPreference pregnancyTargetPreference = (ListPreference) findPreference("profile_pregnancy_target");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String partnerUid = preferences.getString(getString(R.string.pref_pair_uid), null);

        pregnancyTargetPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.equals("partner")) {
                    FirebaseHelper.setPregnancyDate("");
                } else if (newValue.equals("self")) {
                    FirebaseHelper.setPregnancyDate(pregnancyDatePreference.getValue());
                }

                return true;
            }
        });

        FirebaseHelper.requestPregnancyDate(partnerUid, new FirebaseHelper.RequestPregnancyDateEventListener() {
            @Override
            public void onEvent(final String date) {
                if (date.isEmpty()) {
                    return;
                }

                CharSequence[] entries = pregnancyTargetPreference.getEntries();
                CharSequence[] newEntries = new CharSequence[entries.length + 1];
                System.arraycopy(entries, 0, newEntries, 0, entries.length);
                newEntries[entries.length] = String.format("배우자 - 자동 (%s)", date);
                pregnancyTargetPreference.setEntries(newEntries);



                CharSequence[] values = pregnancyTargetPreference.getEntryValues();
                CharSequence[] newValues = new CharSequence[values.length + 1];
                System.arraycopy(values, 0, newValues, 0, values.length);
                newValues[values.length] = String.format("auto");
                pregnancyTargetPreference.setEntryValues(newValues);

                pregnancyTargetPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue.equals("auto")) {
                            pregnancyDatePreference.setValue(date);
                            pregnancyTargetPreference.setValue("partner");
                            FirebaseHelper.setPregnancyDate("");

                            return false;
                        } else if (newValue.equals("partner")) {
                            FirebaseHelper.setPregnancyDate("");
                        } else if (newValue.equals("self")) {
                            FirebaseHelper.setPregnancyDate(pregnancyDatePreference.getValue());
                        }

                        return true;
                    }
                });
            }
        });


        pregnancyDatePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (pregnancyTargetPreference.getValue().equals("self")) {
                    FirebaseHelper.setPregnancyDate((String) newValue);
                }

                return true;
            }
        });

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
                        String value = cursor.getString(0) + " " + cursor.getString(1);
                        editor = selectedContact.getEditor();
                        editor.putString(selectedContact.getKey(), value);
                        selectedContact.setSummary(value);
                        editor.commit();
                        cursor.close();
                    }
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
