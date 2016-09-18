package me.skywave.maternitycarelocker;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

        ImageView imageView1 = (ImageView) findViewById(R.id.imageView2);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView3);

        PackageManager pm = getPackageManager();
        FavoriteManager favorite = new FavoriteManager(this);
        final List<String> packages = favorite.getFavoritePackageNames();

        try {
            ApplicationInfo ai =pm.getApplicationInfo(packages.get(0), 0);
            imageView1.setImageDrawable(ai.loadIcon(pm));
            ai = pm.getApplicationInfo(packages.get(1), 0);
            imageView2.setImageDrawable(ai.loadIcon(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setPackage(packages.get(0));
                intent.setAction(Intent.ACTION_MAIN);
                startActivity(intent);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setPackage(packages.get(1));
                intent.setAction(Intent.ACTION_MAIN);
                startActivity(intent);
            }
        });
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
