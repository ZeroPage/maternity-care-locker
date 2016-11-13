package me.skywave.maternitycarelocker.companion;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.BabyfairVO;
import me.skywave.maternitycarelocker.utils.FirebaseHelper;

public class EventActivity extends AppCompatActivity {
    private List<BabyfairVO> babyfairVOs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        final ListView listView = (ListView) findViewById(R.id.eventListView);
        FirebaseHelper.requestBabyfairList(new FirebaseHelper.RequestBabyfairListEventListener() {

            @Override
            public void onEvent(final List<BabyfairVO> babyfairs) {
                babyfairVOs = babyfairs;
                EventListAdapter eventListAdapter = new EventListAdapter(getApplicationContext(), R.layout.event_list_item, babyfairs);
                listView.setAdapter(eventListAdapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Uri uri = Uri.parse(babyfairVOs.get((int)l).getDetailUrl());

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
