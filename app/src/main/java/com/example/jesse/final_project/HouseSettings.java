package com.example.jesse.final_project;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class HouseSettings extends AppCompatActivity {

    EditText settingText;
    Button submitButton;
    ListView settingsList;
    ArrayList<String> settingsItems;
    ContentValues cV;
    HouseSettingsDBHelper hSDB;
    SQLiteDatabase dB;
    SettingAdapter sAdapter;
    Cursor c;
    protected static final String activity = "House Settings Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_settings);

        settingText = (EditText) findViewById(R.id.settingText);
        submitButton = (Button) findViewById(R.id.submitButton);
        settingsList = (ListView) findViewById(R.id.settingsList);

        cV = new ContentValues();
        hSDB = new HouseSettingsDBHelper(this);
        dB = hSDB.getWritableDatabase();

        settingsItems = new ArrayList<String>();

        sAdapter = new SettingAdapter(this);
        settingsList.setAdapter(sAdapter);
        c = dB.rawQuery("SELECT SETTINGNAME FROM HOUSESETTINGTABLE;", null);
        c.moveToFirst();

        try {
            while (!c.isAfterLast()) {
                settingsItems.add(c.getString(c.getColumnIndex(hSDB.KEY_NAME)));
                c.moveToNext();
            }
        } catch (CursorIndexOutOfBoundsException cioobE) {
            Log.i(activity, "Crashed :" + cioobE.getMessage());
        }

        settingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingText.setText("");
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = settingText.getText().toString();
                if (settingsItems.contains(s)) {                  //If the setting name already exists display a toast message
                    int duration = Toast.LENGTH_SHORT;
                    CharSequence message = "This name for a setting has already been used...";
                    Toast toast = Toast.makeText(HouseSettings.this, message, duration);
                    toast.show();
                } else {                                           //Otherwise add the text to the ArrayList and insert into the DB
                    settingsItems.add(settingText.getText().toString());
                    cV.put(hSDB.getKeyName(), settingText.getText().toString());
                    dB.insert(hSDB.getDATABASE_NAME(), null, cV);
                    sAdapter.notifyDataSetChanged();
                    settingText.setText("");
                }
            }
        });

        final boolean isTablet = findViewById(R.id.settingsFrame) != null;

        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle b = new Bundle();
                b.putLong("id", id);
                String value = settingsList.getItemAtPosition(position).toString();
                b.putString("value", value);
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

        int count = 0;

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
            result = inflater.inflate(R.layout.new_house_setting, null);
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

