package me.skywave.maternitycarelocker.locker.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.core.LockerDialog;
import me.skywave.maternitycarelocker.locker.model.TimerVO;
import me.skywave.maternitycarelocker.locker.view.TimerListAdapter;

public class LockerTimerController extends LockerController {

    public LockerTimerController(Context context) {
        super(R.layout.view_timer, context);
        update();
        prepareTypeFaces();
        prepareTimer();
    }

    public void update() {


    }

    private void prepareTimer() {
        final ToggleButton startButton = (ToggleButton) currentView.findViewById(R.id.onOffButton);
        final ToggleButton stopButton = (ToggleButton) currentView.findViewById(R.id.recordButton);
        final ListView listView = (ListView) currentView.findViewById(R.id.timerListView);
        final TimerListAdapter timerListAdapter = new TimerListAdapter(currentContext, R.layout.timer_list_item, new ArrayList<TimerVO>());
        listView.setAdapter(timerListAdapter);

        startButton.setOnClickListener(new View.OnClickListener() {
            AsyncTask<Void, Long, Void> task = null;
            @Override
            public void onClick(View view) {

                if(startButton.isChecked()) {
                    stopButton.setVisibility(View.VISIBLE);
                    stopButton.setChecked(true);
                    timerListAdapter.setTimerList(new ArrayList<TimerVO>());
                    timerListAdapter.notifyDataSetChanged();

                    final long startTime = System.currentTimeMillis();
                    task = new AsyncTask<Void, Long, Void>() {
                        @Override
                        protected void onProgressUpdate(Long... values) {
                            setTimerText(values[0]);
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            while (!isCancelled()) {
                                long millis = System.currentTimeMillis() - startTime;
                                publishProgress(millis);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    break;
                                }
                            }
                            return null;
                        }
                    };

                    task.execute();
                    //타이머 시작
                } else {
                    stopButton.setVisibility(View.GONE);
                    task.cancel(true);

                    //타이머 정지
                    //TimerListAdapter의 ArrayList를 db에 저장
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stopButton.isChecked()) {
                    timerListAdapter.getTimerList().add(new TimerVO(TimerVO.TYPE_PAIN, "0", "5"));
                    //데이터저장
                    timerListAdapter.notifyDataSetChanged();
                } else {
                    timerListAdapter.getTimerList().add(new TimerVO(TimerVO.TYPE_REST, "0", "5"));
                    //데이터저장
                    timerListAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void prepareTypeFaces() {
        ArrayList<TextView> lightTexts = new ArrayList<>();
        lightTexts.add((TextView) currentView.findViewById(R.id.timerText));

        setTypeFaces(FONT_NOTO_THIN, lightTexts);
    }

    private void setTimerText(long millis) {
        TextView timerText = (TextView) currentView.findViewById(R.id.timerText);

        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        timerText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }
}
