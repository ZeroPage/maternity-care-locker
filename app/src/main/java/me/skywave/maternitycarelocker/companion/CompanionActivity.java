package me.skywave.maternitycarelocker.companion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import me.skywave.maternitycarelocker.locker.LockerService;
import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.companion.preference.SettingsActivity;
import me.skywave.maternitycarelocker.companion.preference.AppSelectActivity;

public class CompanionActivity extends AppCompatActivity {
    private static Intent service = null;
    private int REQ_CODE = 53152;
    private String[] permissions = new String[]{
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQ_CODE) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (Settings.canDrawOverlays(CompanionActivity.this)) {
            toggleService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQ_CODE) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        toggleService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);

        Button buttonService = (Button) findViewById(R.id.button_service);
        Button buttonQuick = (Button) findViewById(R.id.button_quick);
        Button buttonPref = (Button) findViewById(R.id.button_pref);

        buttonService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleService();
            }
        });

        buttonQuick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CompanionActivity.this, AppSelectActivity.class));
            }
        });

        buttonPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CompanionActivity.this, SettingsActivity.class));
            }
        });
    }

    private void toggleService() {
        if (!hasPermissions()) {
            requestPermissions();
            return;
        }

        if (service == null) {
            service = new Intent(CompanionActivity.this, LockerService.class);
            startService(service);
        } else {
            stopService(service);
            service = null;
        }

        Toast.makeText(this, service == null ? "stop" : "start", Toast.LENGTH_SHORT).show();
    }

    private boolean hasPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        return Settings.canDrawOverlays(CompanionActivity.this) && checkSelfPermissions();
    }

    private boolean checkSelfPermissions() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(CompanionActivity.this, permission) ==
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

        if (!Settings.canDrawOverlays(CompanionActivity.this)) {
            Log.e("skywave", "requesting draw");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQ_CODE);
            return;
        }

        if (!checkSelfPermissions()) {
            Log.e("skywave", "requesting others");
            ActivityCompat.requestPermissions(CompanionActivity.this, permissions, REQ_CODE);
            return;
        }
    }
}