package com.betmwitu.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mwongela on 12/15/16.
 */
public class DBHelper {

    //tip table
    public static final String TABLE_TIP = "tip";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TIP_ID = "tip_id";
    public static final String KEY_HOME_TEAM = "home_team";
    public static final String KEY_AWAY_TEAM = "away_team";
    public static final String KEY_DATE = "date";
    public static final String KEY_KICK_OFF = "kick_off";
    public static final String KEY_ODD = "odd";
    public static final String KEY_PREDICTION = "prediction";
    public static final String KEY_SCORE = "score";
    public static final String KEY_RESULT = "result";
    public static final String KEY_BOUGHT = "bought";

    private static final String TAG = DBHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "1315190519.db";

    private DatabaseOpenHelper openHelper;
    private SQLiteDatabase database;

    public DBHelper(Context aContext) {
        openHelper = new DatabaseOpenHelper(aContext);
        database = openHelper.getWritableDatabase();
    }

    public boolean getDuplicateTip(String tip_id) {
        String SQL = "SELECT * FROM " + TABLE_TIP + " WHERE "
                + KEY_TIP_ID + " = '" + tip_id + "'";

        Cursor c = database.rawQuery(SQL, null);
        c.moveToFirst();
        if (c.isFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public void insertTip(String tip_id, String home_team, String away_team, String date, String kick_off,
                          String odd, String prediction, String score, String result) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TIP_ID, tip_id);
        contentValues.put(KEY_HOME_TEAM, home_team);
        contentValues.put(KEY_AWAY_TEAM, away_team);
        contentValues.put(KEY_DATE, date);
        contentValues.put(KEY_KICK_OFF, kick_off);
        contentValues.put(KEY_ODD, odd);
        contentValues.put(KEY_PREDICTION, prediction);
        contentValues.put(KEY_SCORE, score);
        contentValues.put(KEY_RESULT, result);
        database.insert(TABLE_TIP, null, contentValues);
    }

    public void updateTip(String tip_id, String home_team, String away_team, String date, String kick_off,
                          String odd, String prediction, String score, String result) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_HOME_TEAM, home_team);
        contentValues.put(KEY_AWAY_TEAM, away_team);
        contentValues.put(KEY_DATE, date);
        contentValues.put(KEY_KICK_OFF, kick_off);
        contentValues.put(KEY_ODD, odd);
        contentValues.put(KEY_PREDICTION, prediction);
        contentValues.put(KEY_SCORE, score);
        contentValues.put(KEY_RESULT, result);
        database.update(TABLE_TIP, contentValues, KEY_TIP_ID + " = " + tip_id, null);
    }


    public Cursor getAllTips(String date) {
        String SQL = "SELECT * FROM " + TABLE_TIP + " WHERE " + KEY_DATE + " = '" + date + "' " +
                "ORDER BY " + KEY_DATE + " DESC, " + KEY_KICK_OFF + " DESC";
        Cursor tips = database.rawQuery(SQL, null);
        tips.moveToFirst();
        return tips;
    }

    public void buyTip(String tip_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_BOUGHT, "1");
        database.update(TABLE_TIP, contentValues, KEY_TIP_ID + " = " + tip_id, null);
    }

    public DBHelper open() throws SQLException {
        database = openHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        openHelper.close();
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context aContext) {
            super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            String buildSQL = "create table " + TABLE_TIP + " ("
                    + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_TIP_ID + " text, "
                    + KEY_HOME_TEAM + " text, "
                    + KEY_AWAY_TEAM + " text, "
                    + KEY_DATE + " text, "
                    + KEY_KICK_OFF + " text, "
                    + KEY_ODD + " text, "
                    + KEY_PREDICTION + " text, "
                    + KEY_SCORE + " text, "
                    + KEY_RESULT + " text, "
                    + KEY_BOUGHT + " text);";

            Log.d(TAG, "onCreate SQL: " + buildSQL);
            sqLiteDatabase.execSQL(buildSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            // Database schema upgrade code goes here
            String buildSQL = "DROP TABLE IF EXISTS " + TABLE_TIP;
            Log.d(TAG, "onUpgrade SQL: " + buildSQL);

            sqLiteDatabase.execSQL(buildSQL);

            onCreate(sqLiteDatabase);
        }
    }

}