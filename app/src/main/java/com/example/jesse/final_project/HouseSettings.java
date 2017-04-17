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

                    while (!c.isAfterLast()) {
                        valuePair.add(c.getString(c.getColumnIndex(hSDB.SETTING_1)));
                        valuePair.add(c.getString(c.getColumnIndex(hSDB.SETTING_2)));
                        c.moveToNext();
                    }
                    b.putStringArrayList("Values", valuePair);

                    intent.putExtras(b);

                    startActivityForResult(intent, position);


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
                if (nameExists(name)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    AlertDialog dialog;
                    LayoutInflater inflater = getLayoutInflater();
                    View v = inflater.inflate(R.layout.customlayout, null);
                    builder.setTitle("Setting Name Error").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).setView(v);
                    dialog = builder.create();
                    dialog.show();
                } else {
                    settingsItems.add(name);
                    cV.put(hSDB.getID(), name);
                    cV.put(hSDB.SETTING_TYPE, type);
                    dB.insert(hSDB.getTable(), null, cV);
                    sAdapter.notifyDataSetChanged();
                }

                LinearLayout lLayout = (LinearLayout) findViewById(R.id.houseSettings);
                Snackbar.make(lLayout, "This is something I have written", Snackbar.LENGTH_LONG)
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
                        dB.insert(hSDB.getTable(), null, cV);

                        for (int i =0; i<t.size(); i++){
                            cV.clear();
                            String time = t.get(i).substring(0,t.get(i).indexOf("-"));
                            String temp = t.get(i).substring(t.get(i).indexOf(">")+2,t.get(i).length());
                            cV.put(hSDB.getID(), name);
                            cV.put(hSDB.getValue1(), time);
                            cV.put(hSDB.getValue2(), temp);
                            dB.insert(hSDB.getTable(), null, cV);
                        }
                    }
                    break;
                case(2):
                    long id = getIntent().getLongExtra("id",0);
                    hSDB.deleteSetting(dB, settingsItems.get((int)id));
                    settingsItems.remove((int)id);
                    sAdapter.notifyDataSetChanged();
            }
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

}

