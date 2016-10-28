package me.skywave.maternitycarelocker.companion.preference.general;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.Set;

import me.skywave.maternitycarelocker.R;

public class AppSelectActivity extends ListActivity {
    FavoriteListAdapter adapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);
        pref = getSharedPreferences(getResources().getString(R.string.pref_favorite_picker), MODE_PRIVATE);
        editor = pref.edit();

        ListView appList = (ListView) findViewById(android.R.id.list);
        adapter = new FavoriteListAdapter(this, R.layout.favorite_check_item);
        appList.setAdapter(adapter);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> selectedPkg = adapter.getCheckedSet();
                editor.putStringSet(getResources().getString(R.string.pref_favorite_picker), selectedPkg);
                editor.apply();
                finish();
            }
        });
    }
}
