package me.skywave.maternitycarelocker.locker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.zhanghai.android.patternlock.PatternView;

public class LockerCareController {
    private View currentView;
    private TextView unlockActionText;

    public LockerCareController(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        currentView = inflater.inflate(R.layout.view_care, null);
    }

    public View getView() {
        return currentView;
    }
}
