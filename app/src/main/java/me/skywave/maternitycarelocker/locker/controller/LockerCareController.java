package me.skywave.maternitycarelocker.locker.controller;

import android.content.Context;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.InfoManager;
import me.skywave.maternitycarelocker.locker.model.InfoVO;
import me.skywave.maternitycarelocker.locker.view.InfoListAdapter;

public class LockerCareController extends LockerController {
    public LockerCareController(Context context) {
        super(R.layout.view_care, context);
        prepareTypeFaces();
        prepareInfo();
        update();
    }

    public void update() {
    }


    private void prepareTypeFaces() {
        ArrayList<TextView> textViews = new ArrayList<>();

        setTypeFaces(FONT_NOTO_THIN, textViews);
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
                map.put("item2", "tel:" + infos.getContactNum().get(i));
                mlist.add(map);
            }
        }

        if (infos.getBlood() != null) {
            map = new HashMap<>();
            map.put("item1", "혈액형");
            map.put("item2", infos.getBlood());
            mlist.add(map);
        }

        InfoListAdapter adapter = new InfoListAdapter(currentContext, R.layout.info_list_item, mlist);
        listView.setAdapter(adapter);
    }
}
