package me.skywave.maternitycarelocker;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.Set;

public class AppSelectActivity extends ListActivity {
    FavoriteListAdapter adapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);
        pref = getSharedPreferences("favorite", MODE_PRIVATE);
        editor = pref.edit();

        ListView appList = (ListView) findViewById(android.R.id.list);
        adapter = new FavoriteListAdapter(this, R.layout.favorite_check_item);
        appList.setAdapter(adapter);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> selectedPkg = adapter.getCheckedSet();
                editor.putStringSet("favorite", selectedPkg);
                editor.apply();
                finish();
            }
        });
    }
}
