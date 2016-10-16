package me.skywave.maternitycarelocker.locker.controller;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import me.skywave.maternitycarelocker.R;

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
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startButton.isChecked()) {
                    stopButton.setVisibility(View.VISIBLE);
                    stopButton.setChecked(true);
                } else {
                    stopButton.setVisibility(View.GONE);
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stopButton.isChecked()) {

                } else {
                }
            }
        });
    }

    private void prepareTypeFaces() {
        ArrayList<TextView> lightTexts = new ArrayList<>();
        lightTexts.add((TextView) currentView.findViewById(R.id.timerText));

        setTypeFaces(FONT_NOTO_THIN, lightTexts);
    }
}
