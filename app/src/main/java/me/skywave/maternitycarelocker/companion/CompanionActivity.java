package me.skywave.maternitycarelocker.companion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.companion.preference.SettingsActivity;

public class CompanionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_companion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(CompanionActivity.this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
