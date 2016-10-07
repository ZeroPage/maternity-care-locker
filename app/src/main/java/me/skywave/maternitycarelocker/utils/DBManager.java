package me.skywave.maternitycarelocker.utils;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "mcl_db.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_FAVORITE = "favorite";

    private static final String KEY_FAVORITE_ID = "id";
    private static final String KEY_FAVORITE_PKG = "pkg";



    public DBManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITE +
                "(" +
                KEY_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_FAVORITE_PKG + " TEXT" +
                ")";

        db.execSQL(CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_FAVORITE);
            onCreate(db);
        }
    }
}
