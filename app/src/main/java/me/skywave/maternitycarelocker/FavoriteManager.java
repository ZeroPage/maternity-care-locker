package me.skywave.maternitycarelocker;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private Context context;

    public FavoriteManager(Context context) {
        this.context = context;
    }

    public List<String> getFavoritePackageNames() {
        //TODO: get package names from DB
        List<String> packageNames = new ArrayList<>();

        packageNames.add("com.android.calendar");
        packageNames.add("com.android.email");
        return packageNames;
    }
}