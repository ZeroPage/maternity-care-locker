package me.skywave.maternitycarelocker.companion;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.companion.preference.SettingsActivity;
import me.skywave.maternitycarelocker.locker.model.BabyfairVO;
import me.skywave.maternitycarelocker.locker.model.TimerSetVO;
import me.skywave.maternitycarelocker.utils.FirebaseHelper;
import me.skywave.maternitycarelocker.utils.RadioUtil;

public class CompanionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);

        final Button eventButton = (Button) findViewById(R.id.eventButton);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompanionActivity.this, EventActivity.class);

                startActivity(intent);
            }
        });

        ToggleButton button = (ToggleButton) findViewById(R.id.button_music);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RadioUtil.isPlaying()) {
                    RadioUtil.stop();
                } else {
                    RadioUtil.play();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

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

                FirebaseHelper.requestTimerSet(user.getUid(), new FirebaseHelper.RequestTimerSetEventListener() {
                    @Override
                    public void onEvent(List<TimerSetVO> timerSet) {
                        if (timerSet.isEmpty()) {
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

                    }
                });

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CompanionActivity.this);
                final String partnerUid = preferences.getString(getString(R.string.pref_pair_uid), null);

                if (partnerUid != null) {
                    FirebaseHelper.requestTimerSet(partnerUid, new FirebaseHelper.RequestTimerSetEventListener() {
                        @Override
                        public void onEvent(List<TimerSetVO> timerSet) {

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
                    });
                }
            }
        });

        FirebaseHelper.requestBabyfairList(new FirebaseHelper.RequestBabyfairListEventListener() {
            @Override
            public void onEvent(final List<BabyfairVO> babyfairs) {
                TextView fairTitle = (TextView) findViewById(R.id.fairTitle);
                TextView fairDate = (TextView) findViewById(R.id.fairDate);
                Button moreButton = (Button) findViewById(R.id.moreButton);

                DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);

                fairTitle.setText(babyfairs.get(0).getTitle());
                fairDate.setText(format.format(babyfairs.get(0).getDateFrom()) + " ~ " + format.format(babyfairs.get(0).getDateTo()));

                moreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(babyfairs.get(0).getDetailUrl());

                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                moreButton.setVisibility(View.VISIBLE);

            }
        });


        ToggleButton button = (ToggleButton) findViewById(R.id.button_music);

        button.setChecked(RadioUtil.isPlaying());
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
