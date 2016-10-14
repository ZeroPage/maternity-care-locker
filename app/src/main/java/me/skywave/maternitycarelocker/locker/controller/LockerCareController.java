package me.skywave.maternitycarelocker.locker.controller;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.InfoManager;
import me.skywave.maternitycarelocker.locker.model.InfoVO;
import me.skywave.maternitycarelocker.utils.RadioUtil;

public class LockerCareController extends LockerController {
    public LockerCareController(Context context) {
        super(R.layout.view_care, context);
        prepareTypeFaces();
        prepareMusicButton();
        prepareInfo();
        update();
    }

    public void update() {
        prepareAdvice(currentView);
        updateMusicButton();
    }

    private void prepareAdvice(View currentView) {
        TextView adviceText = (TextView) currentView.findViewById(R.id.adviceText);

        try {
            AssetManager am = currentContext.getAssets();
            InputStream is = am.open("week36.json");
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
            }
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

    private void prepareTypeFaces() {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add((TextView) currentView.findViewById(R.id.adviceText));

        setTypeFaces(FONT_NOTO, textViews);
    }

    private void prepareMusicButton() {
        Button button = (Button) currentView.findViewById(R.id.button_music);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RadioUtil.isPlaying()) {
                    RadioUtil.stop();
                } else {
                    RadioUtil.play();
                }

                updateMusicButton();
            }
        });
    }

    private void updateMusicButton() {
        Button button = (Button) currentView.findViewById(R.id.button_music);

        button.setText(RadioUtil.isPlaying() ? "STOP" : "PLAY");
    }

    private void prepareInfo() {
        ListView listView = (ListView) currentView.findViewById(R.id.infoListView);

        InfoVO infos = InfoManager.getInfoVO();

        ArrayList<HashMap<String, String>> mlist = new ArrayList<>();

        HashMap<String, String> map = new HashMap<>();
        if (infos.getName() != null) {
            map.put("item1", "이름");
            map.put("item2", infos.getName());
            mlist.add(map);
        }

        if (infos.getBirth() != null) {
            map = new HashMap<>();
            map.put("item1", "생년월일");
            map.put("item2", infos.getBirth());
            mlist.add(map);
        }

        if (infos.getRecords() != null) {
            map = new HashMap<>();
            map.put("item1", "의료기록");
            map.put("item2", infos.getRecords());
            mlist.add(map);
        }

        if (infos.getAllergies() != null) {
            map = new HashMap<>();
            map.put("item1", "알레르기 및 약물 부작용");
            map.put("item2", infos.getAllergies());
            mlist.add(map);
        }

        if (infos.getMedicines() != null) {
            map = new HashMap<>();
            map.put("item1", "복용 중 약물");
            map.put("item2", infos.getMedicines());
            mlist.add(map);
        }

        if (infos.getContactName().size() > 0) {
            for (int i = 0; i < infos.getContactName().size(); i++) {
                map = new HashMap<>();
                map.put("item1", infos.getContactName().get(i));
                map.put("item2", infos.getContactNum().get(i));
                mlist.add(map);
            }
        }

        if (infos.getBlood() != null) {
            map = new HashMap<>();
            map.put("item1", "혈액형");
            map.put("item2", infos.getBlood());
            mlist.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(currentContext, mlist, android.R.layout.simple_list_item_2,
                new String[]{"item1", "item2"}, new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
    }
}
