package edu.vt.smarttrail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AppStatsDBHelper extends SQLiteOpenHelper {

    public AppStatsDBHelper (Context context) { super(context, "Appstats.db", null, 1); }

    @Override
    public void onCreate(SQLiteDatabase appstats_db) {
        appstats_db.execSQL("CREATE TABLE appstats(pushKey Text primary key, userkey Text, date Text, usageData Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase appstats_db, int olddb, int newdb) {
        appstats_db.execSQL("DROP TABLE IF EXISTS appstats");
    }

    public Boolean insertAppStats(String key, String push_key, String date, String usageData) {
        SQLiteDatabase appstats_db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pushKey", push_key);
        contentValues.put("userKey", key);
        contentValues.put("date", date);
        contentValues.put("usageData", usageData);
        long result = appstats_db.insert("appstats", null, contentValues);
        appstats_db.close();

        return result != -1;
    }

    public ArrayList<AppStats> getAppStats(String key) {
        SQLiteDatabase appstats_db = this.getWritableDatabase();
        Cursor cursor = appstats_db.rawQuery("SELECT * FROM appstats WHERE userkey = ?",
                new String[] {key});
        ArrayList<AppStats> appStatsList = new ArrayList<AppStats>();
        while (cursor.moveToNext()) {
            String push_key = cursor.getString(0);
            String primary_key = cursor.getString(1);
            String date = cursor.getString(2);
            String usageStats = cursor.getString(3);
            AppStats appstats  = new AppStats(primary_key, push_key, date, usageStats);
            appStatsList.add(appstats);
        }
        cursor.close();
        return appStatsList;
    }

    public Boolean checkAppStatsExist(String key) {
        SQLiteDatabase appstats_db = this.getWritableDatabase();
        Cursor cursor = appstats_db.rawQuery("SELECT * FROM appstats WHERE userkey = ?",
                new String[] {key});
        Boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }
}
