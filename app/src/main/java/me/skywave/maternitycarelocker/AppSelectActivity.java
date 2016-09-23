package me.skywave.maternitycarelocker;

import android.app.ListActivity;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AppSelectActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);

        DBManager dbManager = new DBManager(this);
        SQLiteDatabase db = dbManager.getWritableDatabase();

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        ArrayList<String> pkgNames = new ArrayList<>();
        for(PackageInfo pkg : packages) {
            pkgNames.add(pkg.packageName);
        }

        ListView appList = (ListView) findViewById(android.R.id.list);
        FavoriteListAdapter adapter = new FavoriteListAdapter(this, R.layout.favorite_check_item, pkgNames);
        appList.setAdapter(adapter);
    }
}
