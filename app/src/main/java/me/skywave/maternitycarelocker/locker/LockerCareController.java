package me.skywave.maternitycarelocker.locker;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        prepareAdvice(currentView, context);
    }

    private void prepareAdvice(View currentView, Context context) {
        TextView adviceText = (TextView) currentView.findViewById(R.id.adviceText);

        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open("week36.json");
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                Log.d("LK-LOCK", "json line: " + line);
//                response.append('\r');
            }
            Log.d("LK-LOCK", "json string: " + response.toString());
            rd.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            String text = jsonArray.getString((int) (Math.random() * jsonArray.length()));


            adviceText.setText(text);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("LK-LOCK", "Fail to load json");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("LK-LOCK", "Fail to parse json");
        }

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
