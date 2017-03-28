package com.example.jesse.final_project;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class HouseSettings extends AppCompatActivity {

    EditText settingText;
    Button submitButton;
    ListView settingsList;
    ArrayList settingsItems;
    ContentValues cV;
    HouseSettingsDBHelper hSDB;
    SQLiteDatabase dB;

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
                if(settingsItems.contains(s)){                  //If the setting name already exists display a toast message
                    int duration = Toast.LENGTH_SHORT;
                    CharSequence message = "This name for a setting has already been used...";
                    Toast toast = Toast.makeText(HouseSettings.this , message, duration);
                    toast.show();
                }
                else{                                           //Otherwise add the text to the ArrayList and insert into the DB
                    settingsItems.add(settingText.getText().toString());
                    cV.put(hSDB.getKeyName(), settingText.getText().toString());
                    dB.insert(hSDB.getDATABASE_NAME(), null, cV);
                    settingText.setText("");
                }
            }
        });

    }
}
