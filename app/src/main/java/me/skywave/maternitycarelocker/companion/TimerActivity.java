package me.skywave.maternitycarelocker.companion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.TimerVO;
import me.skywave.maternitycarelocker.locker.view.TimerListAdapter;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        final ListView listView = (ListView) findViewById(R.id.timerListView);
        final TimerListAdapter timerListAdapter = new TimerListAdapter(this, R.layout.timer_list_item, new ArrayList<TimerVO>());
        listView.setAdapter(timerListAdapter);

        for(int i = 0; i < 20; i++) {
            TimerVO timerVO = new TimerVO(TimerVO.TYPE_REST, "3", "2");

            timerListAdapter.getTimerList().add(timerVO);
        }
        timerListAdapter.notifyDataSetChanged();
    }
}
