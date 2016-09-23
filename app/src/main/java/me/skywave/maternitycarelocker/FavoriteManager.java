package me.skywave.maternitycarelocker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private Context context;

    public FavoriteManager(Context context) {
        this.context = context;
    }

    public List<String> getFavoritePackageNames() {
        List<String> packageNames = new ArrayList<>();

        DBManager dbManager = new DBManager(context);
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM favorite", null);
        while (c.moveToNext()) {
            packageNames.add(c.getString(1));
        }
        c.close();
        db.close();
        return packageNames;
    }
}