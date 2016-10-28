package me.skywave.maternitycarelocker.companion.preference.general;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import me.skywave.maternitycarelocker.R;

public class AppSelectActivity extends ListActivity {
    MultiSelectionListAdapter adapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_select);
        pref = getSharedPreferences(getResources().getString(R.string.pref_favorite_picker), MODE_PRIVATE);
        editor = pref.edit();

        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        final ArrayList<String> allPkg = new ArrayList<>();
        ArrayList<String> appNames = new ArrayList<>();
        ArrayList<Drawable> appIcons = new ArrayList<>();

        for (PackageInfo pkg : packages) {
            if(pm.getLaunchIntentForPackage(pkg.packageName) != null) {
                ApplicationInfo ai = pkg.applicationInfo;

                allPkg.add(pkg.packageName);
                appNames.add(pm.getApplicationLabel(ai).toString());
                appIcons.add(pm.getApplicationIcon(ai));
            }
        }

        ListView appList = (ListView) findViewById(android.R.id.list);
        adapter = new MultiSelectionListAdapter(this, R.layout.multi_check_item, appNames, appIcons, 2);
        appList.setAdapter(adapter);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iterator<Integer> selected = adapter.getCheckedIndex().iterator();
                Set<String> selectedPkg = new HashSet<>();

                while (selected.hasNext()) {
                    selectedPkg.add(allPkg.get(selected.next()));
                }
                editor.putStringSet(getResources().getString(R.string.pref_favorite_picker), selectedPkg);
                editor.apply();
                finish();
            }
        });
    }
}
