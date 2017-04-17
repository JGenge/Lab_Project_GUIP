package com.example.jesse.final_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HouseSettingsDBHelper extends SQLiteOpenHelper {
    protected static String SETTING_TABLE = "SETTINGTABLE", SETTING_ID = "SETTINGID", SETTING_TYPE = "TYPE", SETTING_1 = "VAL1", SETTING_2 = "VAL2";
    private static String DB_NAME = "SettingsDB";
    protected static int VERSION_NUM = 2;

    public HouseSettingsDBHelper(Context ctx){
        super(ctx, DB_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SETTING_TABLE + " ("+SETTING_ID+" STRING, "+SETTING_1+" STRING, "+SETTING_2
                +" STRING, "+SETTING_TYPE+" STRING);" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ SETTING_TABLE);
        onCreate(db);
    }

    public String getTable(){
        return SETTING_TABLE;
    }

    public String getID() {return SETTING_ID;}

    public String getValue1(){
        return SETTING_1;
    }

    public String getValue2() { return SETTING_2;}

    public void deleteSetting(SQLiteDatabase db, String ID){
        db.execSQL("DELETE FROM "+ SETTING_TABLE+ " WHERE " +SETTING_ID+ " = '"+ID+"'");
    }

}
