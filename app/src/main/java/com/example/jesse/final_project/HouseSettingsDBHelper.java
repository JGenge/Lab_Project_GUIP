package com.example.jesse.final_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Justin on 2017-03-28.
 */

public class HouseSettingsDBHelper extends SQLiteOpenHelper {
    protected static String DATABASE_NAME = "HOUSESETTINGTABLE", KEY_ID = "SETTINGID", KEY_NAME = "SETTINGNAME";
    protected static String KEY_DESC = "DESCRIPTION";
    protected static int VERSION_NUM = 2;

    public HouseSettingsDBHelper(Context ctx){

        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + " ("+KEY_ID+" integer PRIMARY KEY AUTOINCREMENT, "
                +KEY_NAME+" String, "+KEY_DESC+" String);" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
        onCreate(db);
    }

    public String getKeyName(){

        return KEY_NAME;
    }

    public String getDATABASE_NAME(){

        return DATABASE_NAME;
    }

    public String getKeyDesc(){
        return KEY_DESC;
    }
}
