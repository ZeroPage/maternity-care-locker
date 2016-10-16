package me.skywave.maternitycarelocker.locker.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.TimerVO;
import me.skywave.maternitycarelocker.locker.view.TimerListAdapter;

public class LockerTimerController extends LockerController {
    private long middleTime;
    private long startTime;

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

                    startTime = System.currentTimeMillis();
                    middleTime = startTime;
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
                } else {
                    stopButton.setVisibility(View.GONE);
                    task.cancel(true);
                    //TimerListAdapter의 ArrayList를 db에 저장
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stopButton.isChecked()) {
                    long temp = System.currentTimeMillis();
                    timerListAdapter.getTimerList().add(new TimerVO(TimerVO.TYPE_PAIN, convertTime(middleTime-startTime), convertTime(temp-middleTime)));
                    middleTime = temp;

                    timerListAdapter.notifyDataSetChanged();
                } else {
                    long temp = System.currentTimeMillis();
                    timerListAdapter.getTimerList().add(new TimerVO(TimerVO.TYPE_REST, convertTime(middleTime-startTime), convertTime(temp-middleTime)));
                    middleTime = temp;
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
        timerText.setText(convertTime(millis));
    }

    private String convertTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
