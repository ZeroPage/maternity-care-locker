package me.skywave.maternitycarelocker.locker.controller;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.skywave.maternitycarelocker.R;

public abstract class LockerController {
    protected View currentView;
    protected Context currentContext;
    public static final String FONT_NOTO = "NotoSansKR-Light.otf";

    public LockerController(int resource, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        this.currentView = inflater.inflate(resource, null);
        this.currentContext = context;
    }

    public abstract void update();

    public View getView() {
        return currentView;
    }

    protected void setTypeFaces(String fontName, List<TextView> textViews) {
        Typeface type = Typeface.createFromAsset(currentContext.getAssets(), fontName);

        for (TextView textView : textViews) {
            textView.setTypeface(type);
        }
    }

}
