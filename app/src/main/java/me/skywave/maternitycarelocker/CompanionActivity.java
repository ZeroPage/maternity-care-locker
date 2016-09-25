package me.skywave.maternitycarelocker;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CompanionActivity extends AppCompatActivity {
    private static Intent service = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);

        Button buttonService = (Button) findViewById(R.id.button_service);
        Button buttonQuick = (Button) findViewById(R.id.button_quick);

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
