package me.skywave.maternitycarelocker.companion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.TimerSetVO;
import me.skywave.maternitycarelocker.locker.model.TimerVO;
import me.skywave.maternitycarelocker.locker.view.TimerListAdapter;

public class TimerActivity extends AppCompatActivity {
    private Realm realm;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        realm = Realm.getInstance(this);
        id = 0;
        prepareList();
    }

    private void prepareList() {
        final ListView listView = (ListView) findViewById(R.id.timerListView);
        final TimerListAdapter timerListAdapter = new TimerListAdapter(this, R.layout.timer_list_item, new ArrayList<TimerVO>());
        TextView textView = (TextView) findViewById(R.id.dateText);
        RealmResults<TimerSetVO> timerSetVOs = realm.where(TimerSetVO.class).findAll();
        Button prevButton = (Button) findViewById(R.id.prevButton);
        Button nextButton = (Button) findViewById(R.id.nextButton);

        if (timerSetVOs.size() == 0) {
            textView.setText("기록이 없어요");
            prevButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.INVISIBLE);

        } else {

            id = timerSetVOs.size() - 1;

            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    id--;
                    updateList();
                }
            });
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    id++;
                    updateList();
                }
            });

            updateList();
        }
    }

    private void updateList() {
        final ListView listView = (ListView) findViewById(R.id.timerListView);
        final TimerListAdapter timerListAdapter = new TimerListAdapter(this, R.layout.timer_list_item, new ArrayList<TimerVO>());
        TextView textView = (TextView) findViewById(R.id.dateText);
        Button prevButton = (Button) findViewById(R.id.prevButton);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        listView.setAdapter(timerListAdapter);

        RealmResults<TimerSetVO> timerSetVOs = realm.where(TimerSetVO.class).findAll();

        if (id < 1) {
            prevButton.setVisibility(View.INVISIBLE);
        } else {
            prevButton.setVisibility(View.VISIBLE);
        }
        if (id == timerSetVOs.size() - 1) {
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }

        TimerSetVO timerSetVO = timerSetVOs.get(id);
        textView.setText(timerSetVO.getDate());

        for (int i = 0; i < timerSetVO.getTimerVos().size(); i++) {
            TimerVO timerVO = timerSetVO.getTimerVos().get(i);
            timerListAdapter.getTimerList().add(timerVO);
        }
        timerListAdapter.notifyDataSetChanged();

    }
}
