package me.skywave.maternitycarelocker.companion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private int id;
    private List<TimerSetVO> timerSetVOs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        id = 0;
        prepareList();
    }

    private void prepareList() {
        final ListView listView = (ListView) findViewById(R.id.timerListView);
        final TimerListAdapter timerListAdapter = new TimerListAdapter(this, R.layout.timer_list_item, new ArrayList<TimerVO>());
        final TextView textView = (TextView) findViewById(R.id.dateText);
        textView.setText("잠시 기다려 주세요.");

        FirebaseHelper.requestCurrentUser(new FirebaseHelper.RequestUserEventListener() {
            @Override
            public void onEvent(FirebaseUser user) {
                if (user != null) {
                    FirebaseHelper.requestTimerSet(user.getUid(), new FirebaseHelper.RequestTimerSetEventListener() {
                        @Override
                        public void onEvent(List<TimerSetVO> timerSet) {
                            timerSetVOs = timerSet;
                            Button prevButton = (Button) findViewById(R.id.prevButton);
                            Button nextButton = (Button) findViewById(R.id.nextButton);

                            if (timerSetVOs == null || timerSetVOs.size() == 0) {
                                textView.setText("기록이 없어요");
                                prevButton.setVisibility(View.INVISIBLE);
                                nextButton.setVisibility(View.INVISIBLE);

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
