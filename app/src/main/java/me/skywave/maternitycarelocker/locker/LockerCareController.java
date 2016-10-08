package me.skywave.maternitycarelocker.locker;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.zhanghai.android.patternlock.PatternView;

public class LockerCareController {
    private View currentView;

    public LockerCareController(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        currentView = inflater.inflate(R.layout.view_care, null);

        prepareTypeFaces(currentView, "NotoSansKR-Light.otf", context);
    }

    private void prepareTypeFaces(View view, String fontName, Context context) {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add((TextView) view.findViewById(R.id.adviceText));

        Typeface type = Typeface.createFromAsset(context.getAssets(), fontName);

        for (TextView textView : textViews) {
            textView.setTypeface(type);
        }
    }

    public View getView() {
        return currentView;
    }
}
