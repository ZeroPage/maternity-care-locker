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

import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.companion.preference.SettingsActivity;
import me.skywave.maternitycarelocker.locker.model.BabyfairVO;
import me.skywave.maternitycarelocker.locker.model.TimerSetVO;
import me.skywave.maternitycarelocker.utils.FirebaseHelper;

public class CompanionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);
        final Button myTimerButton = (Button) findViewById(R.id.button_my_timer);
        final Button partnerTimerButton = (Button) findViewById(R.id.button_partner_timer);
        final Button sendNotiButton = (Button) findViewById(R.id.send_notification);
        final Button eventButton = (Button) findViewById(R.id.eventButton);

        myTimerButton.setVisibility(View.GONE);
        partnerTimerButton.setVisibility(View.GONE);

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompanionActivity.this, EventActivity.class);

                startActivity(intent);
            }
        });


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

                        myTimerButton.setVisibility(View.VISIBLE);
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

                            partnerTimerButton.setVisibility(View.VISIBLE);
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

        final Intent intent = new Intent(this, TimerActivity.class);
        sendNotiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setSmallIcon(android.R.drawable.ic_notification_clear_all);
                builder.setContentTitle("테스트");
                builder.setContentText("테스트");
                builder.setContentIntent(resultPendingIntent);
                builder.setAutoCancel(true);

                NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notiManager.notify(001, builder.build());
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
