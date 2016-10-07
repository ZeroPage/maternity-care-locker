package me.skywave.maternitycarelocker.companion.preference;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FavoriteManager {
    private Context context;

    public FavoriteManager(Context context) {
        this.context = context;
    }

    public List<String> getFavoritePackageNames() {
        List<String> packageNames = new ArrayList<>();

        SharedPreferences pref = context.getSharedPreferences("favorite", Context.MODE_PRIVATE);
        packageNames.addAll(pref.getStringSet("favorite", new HashSet<String>()));
        return packageNames;
    }
}