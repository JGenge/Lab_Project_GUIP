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

        fr = chooseFrag(id);

        if(fr != null) {
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
            View v=inflater.inflate(R.layout.error_window, null);

            builder.setTitle(R.string.dialogTitle).setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            }).setView(v);
            dialog = builder.create();
            dialog.show();
        }

    }

    private Fragment chooseFrag(long id){

        Fragment frag;

        if (id == 0){
            frag = new GarageSettings();
        }
        else if  (id == 1){
            frag = new TemperatureSettings();
        }
        else if (id == 2){
            frag = new OutsideTemperature();
        }
        else{
            frag = null;
        }

        return frag;
    }
}
