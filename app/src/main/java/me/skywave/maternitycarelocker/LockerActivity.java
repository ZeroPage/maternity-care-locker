package me.skywave.maternitycarelocker;

import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LockerActivity extends AppCompatActivity {
    private TextView weatherTextView;
    private TextClock textClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        weatherTextView = (TextView)findViewById(R.id.weatherText);
        textClock = (TextClock) findViewById(R.id.textClock);
        initTypeFace();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTypeFace() {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(textClock);
        textViews.add(weatherTextView);

        setTypefaces(textViews);
    }

    private void setTypefaces(ArrayList<TextView> textViews, String fontName) {
        Typeface type = Typeface.createFromAsset(this.getAssets(), fontName);
        for(TextView textView :  textViews) {
            textView.setTypeface(type);
        }
    }

    private void setTypefaces(ArrayList<TextView> textViews) {
        setTypefaces(textViews, "NotoSansKR-Light.otf");
    }

}
