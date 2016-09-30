package me.skywave.maternitycarelocker;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CompanionActivity extends AppCompatActivity {
    private static Intent service = null;
    private int REQ_CODE = 53152;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode != REQ_CODE) {
                return;
            }

            if (Settings.canDrawOverlays(CompanionActivity.this)) {
                toggleService();
            } else {
                Log.e("Permission", "fail");
            }
        }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(CompanionActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQ_CODE);
                    }
                }

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
        if (service == null) {
            service = new Intent(CompanionActivity.this, LockerService.class);
            startService(service);
        } else {
            stopService(service);
            service = null;
        }

        Toast.makeText(this, service == null ? "stop" : "start", Toast.LENGTH_SHORT).show();
    }
}
