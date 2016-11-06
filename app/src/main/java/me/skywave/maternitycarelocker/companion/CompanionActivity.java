package me.skywave.maternitycarelocker.companion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseUser;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.companion.preference.SettingsActivity;
import me.skywave.maternitycarelocker.utils.FirebaseHelper;

public class CompanionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);
        final Button myTimerButton = (Button) findViewById(R.id.button_my_timer);
        final Button partnerTimerButton = (Button) findViewById(R.id.button_partner_timer);

        myTimerButton.setEnabled(false);
        partnerTimerButton.setEnabled(false);


        FirebaseHelper.requestCurrentUser(new FirebaseHelper.RequestUserEventListener() {
            @Override
            public void onEvent(final FirebaseUser user) {
                if (user == null) {
                    return;
                }

                myTimerButton.setEnabled(true);
                myTimerButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CompanionActivity.this, TimerActivity.class);
                        intent.putExtra(TimerActivity.BUNDLE_STRING_UID, user.getUid());

                        startActivity(intent);
                    }
                });

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CompanionActivity.this);
                final String partnerUid = preferences.getString(getString(R.string.pref_pair_uid), null);

                if (partnerUid != null) {
                    partnerTimerButton.setEnabled(true);
                    partnerTimerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CompanionActivity.this, TimerActivity.class);

                            intent.putExtra(TimerActivity.BUNDLE_STRING_UID, partnerUid);

                            startActivity(intent);
                        }
                    });
                }
            }
        });

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
