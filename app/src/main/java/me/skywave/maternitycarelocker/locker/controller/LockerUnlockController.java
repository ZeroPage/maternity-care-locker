package me.skywave.maternitycarelocker.locker.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.core.LockerDialog;
import me.zhanghai.android.patternlock.PatternView;

public class LockerUnlockController extends LockerController {
    private TextView unlockActionText;
    private PatternView pattern;

    public LockerUnlockController(Context context) {
        super(R.layout.view_unlock, context);

        prepareButtons();
        preparePattern();
    }

    @Override
    public void update() {
        pattern.clearPattern();
    }

    private void preparePattern() {
        pattern = (PatternView) currentView.findViewById(R.id.pattern_view);
        unlockActionText = (TextView) currentView.findViewById(R.id.text_unlock_action);

        pattern.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<PatternView.Cell> cells) {

            }

            @Override
            public void onPatternDetected(List<PatternView.Cell> cells) {
                if (cells.size() == 3 &&
                        cells.get(0) == PatternView.Cell.of(2, 0) &&
                        cells.get(1) == PatternView.Cell.of(2, 1) &&
                        cells.get(2) == PatternView.Cell.of(2, 2)
                        ) {
                    LockerDialog.unlock();
                } else {
                    pattern.setDisplayMode(PatternView.DisplayMode.Wrong);
                }
            }
        });
    }

    private void prepareButtons() {
        Button cancelButton = (Button) currentView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockerDialog.requestWidget();
            }
        });
    }

    public void setUnlockActionName(String actionName) {
        unlockActionText.setText(actionName);
    }
}
