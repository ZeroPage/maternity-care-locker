package me.skywave.maternitycarelocker.locker.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.core.LockerDialog;
import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;

public class LockerUnlockController extends LockerController {
    private String patternHash;

    private TextView unlockActionText;
    private PatternView pattern;

    public LockerUnlockController(Context context, String patternHash) {
        super(R.layout.view_unlock, context);

        this.patternHash = patternHash;

        if (this.patternHash.isEmpty()) {
            hideAll();
        } else {
            prepareButtons();
            preparePattern();
        }
    }

    @Override
    public void update() {
        if (pattern != null) {
            pattern.clearPattern();
        }
    }

    private void hideAll() {
        currentView.setVisibility(View.GONE);
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
                if (PatternUtils.patternToSha1String(cells).equals(patternHash)) {
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
                update();
            }
        });
    }

    public void setUnlockActionName(String actionName) {
        if (unlockActionText != null) {
            unlockActionText.setText(actionName);
        }
    }
}
