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

    public class LockerInfoController extends LockerController {
        public LockerInfoController(Context context) {
            super(R.layout.view_info, context);
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

        InfoManager infoManager = new InfoManager(currentContext);
        InfoVO infos = infoManager.getInfoVO();

        ArrayList<HashMap<String, String>> mlist = new ArrayList<>();

        HashMap<String, String> map = new HashMap<>();
        if (!infos.getName().isEmpty()) {
            map.put("item1", "이름");
            map.put("item2", infos.getName());
            mlist.add(map);
        }

        if (!infos.getBirth().isEmpty()) {
            map = new HashMap<>();
            map.put("item1", "생년월일");
            map.put("item2", infos.getBirth());
            mlist.add(map);
        }

        if (!infos.getPregnancyDate().isEmpty()) {
            map = new HashMap<>();
            map.put("item1", "임신일");
            map.put("item2", infos.getPregnancyDate());
            mlist.add(map);
        }

        if (!infos.getRecords().isEmpty()) {
            map = new HashMap<>();
            map.put("item1", "의료기록");
            map.put("item2", infos.getRecords());
            mlist.add(map);
        }

        if (!infos.getAllergies().isEmpty()) {
            map = new HashMap<>();
            map.put("item1", "알레르기 및 약물 부작용");
            map.put("item2", infos.getAllergies());
            mlist.add(map);
        }

        if (!infos.getMedicines().isEmpty()) {
            map = new HashMap<>();
            map.put("item1", "복용 중 약물");
            map.put("item2", infos.getMedicines());
            mlist.add(map);
        }

        if (!infos.getComContact().getName().isEmpty()) {
            map = new HashMap<>();
            map.put("item1", "가족: " + infos.getComContact().getName());
            map.put("item2", "tel: " + infos.getComContact().getNumber());
            mlist.add(map);
        }

        if (!infos.getHptContact().getName().isEmpty()) {
            map = new HashMap<>();
            map.put("item1", "병원: " + infos.getHptContact().getName());
            map.put("item2", "tel: " + infos.getHptContact().getNumber());
            mlist.add(map);
        }

        if (!infos.getEtcContact().getName().isEmpty()) {
            map = new HashMap<>();
            map.put("item1", infos.getEtcContact().getName());
            map.put("item2", "tel: " + infos.getEtcContact().getNumber());
            mlist.add(map);
        }

        if (!infos.getBlood().isEmpty()) {
            map = new HashMap<>();
            map.put("item1", "혈액형");
            map.put("item2", infos.getBlood());
            mlist.add(map);
        }

        InfoListAdapter adapter = new InfoListAdapter(currentContext, R.layout.info_list_item, mlist);
        listView.setAdapter(adapter);
    }
}
