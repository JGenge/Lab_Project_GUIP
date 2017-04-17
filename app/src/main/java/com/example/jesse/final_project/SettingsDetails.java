package com.example.jesse.final_project;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class SettingsDetails extends AppCompatActivity  {
    Fragment fr;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fragment);
        FragmentManager fMan = getFragmentManager();
        FragmentTransaction fTran = fMan.beginTransaction();
        Bundle b = getIntent().getExtras();
        long id = b.getLong("id");
        String type = b.getString("Type");
        String name = b.getString("Name");

        switch (type){

            case "Garage":
                fr = new GarageSettings();
                break;
            case "Temperature":
                fr = new TemperatureSettings();
                break;
            case "Outdoor":
                fr = new OutsideTemperature();
                break;
            case "New":
                fr = new NewSetting();
                break;
        }

        if(fr != null) {
            fr.setArguments(b);
            fTran.replace(R.id.settingsFrame, fr);
            fTran.commit();
            if (fr instanceof OutsideTemperature){
                ((OutsideTemperature) fr).executeQuery();
            }
        }
        else{

            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            AlertDialog dialog;

            LayoutInflater inflater = getLayoutInflater();
            View v=inflater.inflate(R.layout.customlayout, null);

            builder.setTitle(R.string.dialogTitle).setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            }).setView(v);
            dialog = builder.create();
            dialog.show();
        }

    }
}
