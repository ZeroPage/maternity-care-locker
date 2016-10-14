package me.skywave.maternitycarelocker.companion.preference;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.core.LockerService;
import me.skywave.maternitycarelocker.utils.ImageFilePath;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A backgroundPreference value change listener that updates the backgroundPreference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the backgroundPreference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a backgroundPreference's summary to its value. More specifically, when the
     * backgroundPreference's value is changed, its summary (line of text below the
     * backgroundPreference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of backgroundPreference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the backgroundPreference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        private static Intent service = null;
        private static final int REQUEST_CODE_PERMISSION = 53152;
        private static final int REQUEST_CODE_PICK_IMAGE = 1;

        private String[] permissions = new String[]{
                Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CALENDAR, Manifest.permission.READ_EXTERNAL_STORAGE
        };

        SwitchPreference serviceSwitch;
        Preference backgroundPreference;
        Preference favoritePreference;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            serviceSwitch = (SwitchPreference) findPreference(getResources().getString(R.string.pref_service_switch));
            serviceSwitch.setChecked(service != null);

            serviceSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    toggleService();

                    return true;
                }
            });

            backgroundPreference = findPreference(getResources().getString(R.string.pref_background_picker));
            backgroundPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "배경을 선택할 앱"), REQUEST_CODE_PICK_IMAGE);
                    return true;
                }
            });

            favoritePreference = findPreference(getResources().getString(R.string.pref_favorite_picker));
            favoritePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), AppSelectActivity.class);
                    startActivity(intent);
                    return false;
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

            if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
                Log.d("LK-LOCK", "image picker data: " + data.getData().toString());
                Uri selectedImage = Uri.parse(data.getData().toString());

                String selectedImagePath = ImageFilePath.getPath(backgroundPreference.getContext(), selectedImage);
                Log.i("LK-LOCK", "converted path:" + selectedImagePath);

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(backgroundPreference.getContext());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(backgroundPreference.getKey(), selectedImagePath);
                editor.apply();
            } else if (requestCode == REQUEST_CODE_PERMISSION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (Settings.canDrawOverlays(getContext())) {
                    toggleService();
                }
            }
        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == REQUEST_CODE_PERMISSION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean canToggle = true;

                for (int result : grantResults) {
                    canToggle &= result == PackageManager.PERMISSION_GRANTED;
                }

                if (canToggle) {
                    toggleService();
                }
            }
        }

        private boolean hasPermissions() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }

            return Settings.canDrawOverlays(getContext()) && checkSelfPermissions();
        }

        private boolean checkSelfPermissions() {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) ==
                        PermissionChecker.PERMISSION_DENIED) {
                    return false;
                }
            }

            return true;
        }

        private void requestPermissions() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return;
            }

            if (!Settings.canDrawOverlays(getContext())) {
                Log.e("skywave", "requesting draw");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getContext().getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_PERMISSION);
                return;
            }

            if (!checkSelfPermissions()) {
                Log.e("skywave", "requesting others");
                requestPermissions(permissions, REQUEST_CODE_PERMISSION);
            }
        }

        private void toggleService() {
            if (!hasPermissions()) {
                requestPermissions();
                updateServiceSwitchPreference();
                return;
            }

            if (service == null) {
                service = new Intent(getActivity(), LockerService.class);
                getActivity().startService(service);
            } else {
                getActivity().stopService(service);
                service = null;
            }

            updateServiceSwitchPreference();
        }

        private void updateServiceSwitchPreference() {
            boolean running = service != null;
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                    .putBoolean(getString(R.string.pref_service_switch), running).apply();

            serviceSwitch.setChecked(running);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
            bindPreferenceSummaryToValue(findPreference("example_text"));
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
    }

}
