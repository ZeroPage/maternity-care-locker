package me.skywave.maternitycarelocker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.skywave.maternitycarelocker.R;

public class FavoriteManager {
    private Context context;

    public FavoriteManager(Context context) {
        this.context = context;
    }

    public List<String> getFavoritePackageNames() {
        List<String> packageNames = new ArrayList<>();

        SharedPreferences pref = context.getSharedPreferences(context.getResources().getString(R.string.pref_favorite_picker), Context.MODE_PRIVATE);
        packageNames.addAll(pref.getStringSet(context.getResources().getString(R.string.pref_favorite_picker), new HashSet<String>()));
        return packageNames;
    }
}