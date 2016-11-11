package me.skywave.maternitycarelocker.companion.preference.general;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.MenuItem;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.companion.preference.SettingsActivity;
import me.skywave.maternitycarelocker.locker.core.LockerService;
import me.skywave.maternitycarelocker.utils.ImageFilePath;

import static android.app.Activity.RESULT_OK;

public class GeneralPreferenceFragment extends PreferenceFragment {
    private static Intent service = null;
    private final int REQUEST_CODE_PERMISSION = 53152;
    private final int REQUEST_CODE_PICK_IMAGE = 1;

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