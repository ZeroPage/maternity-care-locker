package me.skywave.maternitycarelocker;

import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class AppSelectActivity extends ListActivity {
    FavoriteListAdapter adapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);

        DBManager dbManager = new DBManager(this);
        db = dbManager.getWritableDatabase();

        ListView appList = (ListView) findViewById(android.R.id.list);
        adapter = new FavoriteListAdapter(this, R.layout.favorite_check_item);
        appList.setAdapter(adapter);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selectedPkg = adapter.getCheckedList();
                if (selectedPkg.size() != 0) {
                    db.delete("favorite", null, null);
                    for (int i = 0; i < selectedPkg.size(); i++) {
                        db.execSQL("INSERT INTO " + "favorite (pkg)" + " VALUES ('" + selectedPkg.get(i) + "');");
                    }
                }
                finish();
            }
        });
    }
}
