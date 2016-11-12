package me.skywave.maternitycarelocker.companion;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.TimerSetVO;
import me.skywave.maternitycarelocker.locker.model.TimerVO;
import me.skywave.maternitycarelocker.locker.view.TimerListAdapter;
import me.skywave.maternitycarelocker.utils.FirebaseHelper;

public class TimerActivity extends AppCompatActivity {
    public static final String BUNDLE_STRING_UID = "str_uid";

    private int id;
    private List<TimerSetVO> timerSetVOs;
    private String uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
    }

    @Override
    protected void onResume() {
        super.onResume();

        id = 0;
        uid = getIntent().getStringExtra(BUNDLE_STRING_UID);
        if (uid == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            uid = preferences.getString(getString(R.string.pref_pair_uid), null);
        }
        prepareList();
    }

    private void prepareList() {
        Log.d("skywave", uid);

        final ListView listView = (ListView) findViewById(R.id.timerListView);
        final TimerListAdapter timerListAdapter = new TimerListAdapter(this, R.layout.timer_list_item, new ArrayList<TimerVO>());
        final TextView textView = (TextView) findViewById(R.id.dateText);
        textView.setText("잠시 기다려 주세요.");

        final Button prevButton = (Button) findViewById(R.id.prevButton);
        final Button nextButton = (Button) findViewById(R.id.nextButton);

        prevButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);


        FirebaseHelper.requestCurrentUser(new FirebaseHelper.RequestUserEventListener() {
            @Override
            public void onEvent(FirebaseUser user) {
                if (user != null) {
                    FirebaseHelper.requestTimerSet(uid, new FirebaseHelper.RequestTimerSetEventListener() {
                        @Override
                        public void onEvent(List<TimerSetVO> timerSet) {
                            timerSetVOs = timerSet;
                            if (timerSetVOs == null || timerSetVOs.size() == 0) {
                                textView.setText("기록이 없어요");

                            } else {

                                id = timerSetVOs.size() - 1;

                                prevButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        id--;
                                        updateList();
                                    }
                                });
                                nextButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        id++;
                                        updateList();
                                    }
                                });

                                updateList();
                            }
                        }
                    });
                } else {
                    textView.setText("계정이 존재하지 않아요.");
                }
            }
        });

    }

    private void updateList() {
        final ListView listView = (ListView) findViewById(R.id.timerListView);
        final TimerListAdapter timerListAdapter = new TimerListAdapter(this, R.layout.timer_list_item, new ArrayList<TimerVO>());
        TextView textView = (TextView) findViewById(R.id.dateText);
        Button prevButton = (Button) findViewById(R.id.prevButton);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        listView.setAdapter(timerListAdapter);

        if (id < 1) {
            prevButton.setVisibility(View.INVISIBLE);
        } else {
            prevButton.setVisibility(View.VISIBLE);
        }
        if (id == timerSetVOs.size() - 1) {
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }

        TimerSetVO timerSetVO = timerSetVOs.get(id);
        textView.setText(timerSetVO.getDate());

        for (int i = 0; i < timerSetVO.getTimerVos().size(); i++) {
            TimerVO timerVO = timerSetVO.getTimerVos().get(i);
            timerListAdapter.getTimerList().add(timerVO);
        }
        timerListAdapter.notifyDataSetChanged();

    }
}
