package me.skywave.maternitycarelocker.locker.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.TimerSetVO;
import me.skywave.maternitycarelocker.locker.model.TimerVO;
import me.skywave.maternitycarelocker.locker.view.TimerListAdapter;
import me.skywave.maternitycarelocker.utils.FirebaseHelper;

public class LockerTimerController extends LockerController {
    private long middleTime;
    private long startTime;

    public LockerTimerController(Context context) {
        super(R.layout.view_timer, context, "진통타이머");
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

                    startTime = (System.currentTimeMillis() / 1000);
                    middleTime = startTime;
                    task = new AsyncTask<Void, Long, Void>() {
                        @Override
                        protected void onProgressUpdate(Long... values) {
                            setTimerText(values[0]);
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            while (!isCancelled()) {
                                long second = (System.currentTimeMillis() / 1000) - startTime;
                                publishProgress(second);
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

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    TimerSetVO timerSetVO = new TimerSetVO(sdf.format(new Date()), timerListAdapter.getTimerList());
                    FirebaseHelper.addTimerSet(timerSetVO);
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long temp = System.currentTimeMillis() / 1000;
                TimerVO timerVO;
                if(stopButton.isChecked()) {
                    timerVO = new TimerVO(TimerVO.TYPE_REST, convertTime(middleTime-startTime), convertTime(temp-middleTime));
                } else {
                    timerVO = new TimerVO(TimerVO.TYPE_PAIN, convertTime(middleTime-startTime), convertTime(temp-middleTime));
                }
                timerListAdapter.getTimerList().add(timerVO);
                middleTime = temp;
                timerListAdapter.notifyDataSetChanged();
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

    private String convertTime(long seconds) {
        int minutes = (int) (seconds / 60);
        int hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
