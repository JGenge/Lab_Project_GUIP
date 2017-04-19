package com.example.jesse.final_project;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.support.design.widget.Snackbar;

public class HouseSettings extends AppCompatActivity {

    ListView settingsList;
    ArrayList<String> settingsItems;
    ContentValues cV;
    HouseSettingsDBHelper hSDB;
    SQLiteDatabase dB;
    SettingAdapter sAdapter;
    Cursor c;
    protected static final String activity = "House Settings Activity";
    String table, setting;
    Bundle b;
    Button newButton;
    long dID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_settings);


        settingsList = (ListView) findViewById(R.id.settingsList);
        cV = new ContentValues();
        hSDB = new HouseSettingsDBHelper(this);
        dB = hSDB.getWritableDatabase();
        settingsItems = new ArrayList<String>();
        sAdapter = new SettingAdapter(this);
        settingsList.setAdapter(sAdapter);
        newButton = (Button) findViewById(R.id.createSetting);

        table = hSDB.getTable();
        setting = hSDB.getID();

        c = dB.rawQuery("SELECT " + setting + " FROM " + table + ";", null);
        c.moveToFirst();

        try {
            while (!c.isAfterLast()) {
                String name = c.getString(c.getColumnIndex(hSDB.SETTING_ID));
                if (!settingsItems.contains(name) && name != null) {
                    settingsItems.add(name);
                }
                c.moveToNext();
            }
        } catch (CursorIndexOutOfBoundsException cioobE) {
            Log.i(activity, "Crashed :" + cioobE.getMessage());
        }

        final boolean isTablet = findViewById(R.id.settingsFrame) != null;

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b = new Bundle();
                b.putString("Type", "New");
                Intent intent = new Intent(HouseSettings.this, SettingsDetails.class);
                intent.putExtras(b);
                startActivityForResult(intent, 1);
            }
        });

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = settingsItems.get((int) id);

                c = dB.rawQuery("SELECT " + hSDB.SETTING_1 + ", " + hSDB.SETTING_2 + ", " + hSDB.SETTING_TYPE + " FROM " + hSDB.SETTING_TABLE +
                        " WHERE " + hSDB.SETTING_ID + " = '" + name +"'", null);
                c.moveToFirst();

                try {

                    Log.i(activity, ""+c.getString(c.getColumnIndex(hSDB.SETTING_TYPE)));
                    String type = c.getString(c.getColumnIndex(hSDB.SETTING_TYPE));
                    b = new Bundle();
                    b.putString("Name", name);
                    b.putString("Type", type);
                    b.putLong("id", id);
                    ArrayList<String> valuePair = new ArrayList<String>();
                    Intent intent = new Intent(HouseSettings.this, SettingsDetails.class);

                    if (type.equals("Temperature")){
                    while (!c.isAfterLast()) {
                        String time = c.getString(c.getColumnIndex(hSDB.SETTING_1));
                        String temp = c.getString(c.getColumnIndex(hSDB.SETTING_2));
                        if (time != null && !time.equals("Current")) {
                            valuePair.add(time + " -> " + temp);
                        }

                        c.moveToNext();
                    }
                    }
                    else if(type.equals("Garage")){
                            String door = c.getString(c.getColumnIndex(hSDB.SETTING_1));
                            String light = c.getString(c.getColumnIndex(hSDB.SETTING_2));
                            valuePair.add(door);
                            valuePair.add(light);
                    }
                    b.putStringArrayList("Values", valuePair);

                    intent.putExtras(b);

                  //  startActivityForResult(intent, position);


                } catch (CursorIndexOutOfBoundsException cioobE) {
                    Log.i(activity, "Crashed :" + cioobE.getMessage());
                }

                if (!isTablet) {
                    Intent intent = new Intent(HouseSettings.this, SettingsDetails.class);
                    intent.putExtras(b);
                    Log.i(activity, "Item ID " + id);
                    startActivityForResult(intent, position);
                } else {
                    FragmentManager fMan = getFragmentManager();
                    FragmentTransaction fTran = fMan.beginTransaction();
                    Fragment fr = new SettingsFragment();

                    fr.setArguments(b);
                    fTran.replace(R.id.settingsFrame, fr);
                    fTran.commit();
                    }
                }
            });
        }

        public void onActivityResult(int req, int res, Intent data) {

            cV.clear();

            switch (res) {

                case(0):
                String name = data.getStringExtra("Name");
                String type = data.getStringExtra("Type");



                    settingsItems.add(name);
                    cV.put(hSDB.getID(), name);
                    cV.put(hSDB.SETTING_TYPE, type);
                    dB.insert(hSDB.getTable(), null, cV);
                    sAdapter.notifyDataSetChanged();


                LinearLayout lLayout = (LinearLayout) findViewById(R.id.houseSettings);
                Snackbar.make(lLayout, "New setting "+name+" added.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;

                case(1):

                    if (data.getStringArrayListExtra("Temps")!=null){
                        ArrayList<String> t = new ArrayList<String>();
                        name = data.getStringExtra("Name");
                        t = data.getStringArrayListExtra("Temps");
                        String cT = data.getStringExtra("Current");
                        cV.put(hSDB.getID(), name);
                        cV.put(hSDB.getValue1(), "Current");
                        cV.put(hSDB.getValue2(), cT);
                        if (settingExists(name, "Current")){
                            String[] args = {name, "Current"};
                            dB.update(hSDB.getTable(), cV, hSDB.getID()+" =? AND "+hSDB.getValue1()+"=?",args);
                        }
                        else
                            dB.insert(hSDB.getTable(), null, cV);

                        for (int i =0; i<t.size(); i++){
                            cV.clear();
                            String time = t.get(i).substring(0,t.get(i).indexOf("-"));
                            String temp = t.get(i).substring(t.get(i).indexOf(">")+2,t.get(i).length());
                            cV.put(hSDB.getID(), name);
                            cV.put(hSDB.getValue1(), time);
                            cV.put(hSDB.getValue2(), temp);
                            if(settingExists(name, time)){
                                String[] args = {name, time};
                                dB.update(hSDB.getTable(), cV, hSDB.getID()+" =? AND "+hSDB.getValue1()+"=?",args);
                            }
                            else
                                dB.insert(hSDB.getTable(), null, cV);
                        }
                    }

                    if(data.getStringExtra("Door")!=null){
                        name = data.getStringExtra("Name");
                        String door = data.getStringExtra("Door");
                        String light = data.getStringExtra("Light");
                        cV.put(hSDB.getID(), name);
                        cV.put(hSDB.getValue1(), door);
                        cV.put(hSDB.getValue2(), light);
                        if(settingExists(name)){
                            String args[] = {name};
                            dB.update(hSDB.getTable(), cV, hSDB.getID()+" =?",args);
                        }
                        else
                            dB.insert(hSDB.getTable(), null, cV);
                    }

                    break;

                case(2):
                    long dID = data.getLongExtra("id",0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    AlertDialog dialog;
                    LayoutInflater inflater = getLayoutInflater();
                    View v = inflater.inflate(R.layout.customlayout, null);
                    builder.setTitle("Delete "+settingsItems.get((int)dID)+"?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteID();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                        }).setView(v);

                    dialog = builder.create();
                    dialog.show();
            }
        }

        private void deleteID(){
            String name = settingsItems.get((int)dID);
            hSDB.deleteSetting(dB, settingsItems.get((int)dID));
            settingsItems.remove((int)dID);
            sAdapter.notifyDataSetChanged();
            LinearLayout lLayout = (LinearLayout) findViewById(R.id.houseSettings);
            Snackbar.make(lLayout, "Setting "+name+" deleted.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        public boolean nameExists(String name){
            c = dB.rawQuery("SELECT "+hSDB.SETTING_ID+" FROM "+hSDB.getTable(), null);

            boolean match = false;
            try {
                while (!c.isAfterLast()) {
                    String result = c.getString(c.getColumnIndex(hSDB.SETTING_ID));
                    if (result.equals(name)) {
                        match = true;
                        break;
                    }
                c.moveToNext();
                }
            } catch (CursorIndexOutOfBoundsException cioobE) {
            Log.i(activity, "Crashed :" + cioobE.getMessage());
            }

        return match;

        }



    private class SettingAdapter extends ArrayAdapter<String> {
        public SettingAdapter(Context context) {
            super(context, 0);
        }

        public int getCount() {
            return settingsItems.size();
        }

        public String getItem(int position) {
            return settingsItems.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = HouseSettings.this.getLayoutInflater();
            View result = null;
            result = inflater.inflate(R.layout.setting, null);
            TextView message = (TextView) result.findViewById(R.id.setting_text);
            message.setText(getItem(position));
            return result;


        }

    }

    @Override
    protected void onDestroy() {
        dB.close();
        super.onDestroy();
    }

    private boolean settingExists(String set, String val){
        boolean match = false;
        c = dB.rawQuery("SELECT " + hSDB.SETTING_ID + " FROM " + hSDB.SETTING_TABLE +
                " WHERE " + hSDB.SETTING_ID + " = '" + set +"' AND "+hSDB.SETTING_1+" = '"+val+"'", null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            String s = c.getString(c.getColumnIndex(hSDB.SETTING_ID));
            if(set.equals(s)){
                match = true;
                break;
            }
        }
        return match;
    }

    private boolean settingExists(String set){
        boolean match = false;
        c = dB.rawQuery("SELECT " + hSDB.SETTING_ID + " FROM " + hSDB.SETTING_TABLE +
                " WHERE " + hSDB.SETTING_ID + " = '" + set +"'", null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            String s = c.getString(c.getColumnIndex(hSDB.SETTING_ID));
            if(set.equals(s)){
                match = true;
                break;
            }
        }
        return match;
    }

}

