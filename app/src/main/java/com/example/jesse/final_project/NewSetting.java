package com.example.jesse.final_project;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class NewSetting extends Fragment{

    View root;
    EditText settingText;
    Button newSettingButton;
    RadioGroup rGroup;
    RadioButton rButton;
    HouseSettings hS;

    private static final String activity = "New Setting";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.new_setting, container, false);

        settingText = (EditText) root.findViewById(R.id.newSettingText);
        newSettingButton = (Button) root.findViewById(R.id.newSettingButton);
        rGroup = (RadioGroup) root.findViewById(R.id.radios);

        settingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingText.setText("");
            }
        });

        newSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            int selectedId = rGroup.getCheckedRadioButtonId();
            rButton = (RadioButton) root.findViewById(selectedId);
            String setting = rButton.getText().toString();
            String name = settingText.getText().toString();
            settingText.setText("");
            String type = "";

            switch(setting){
                case "New Garage Setting":
                    type = "Garage";
                    break;
                case "New Temperature Setting":
                    type = "Temperature";
                    break;
                case "New Outdoor Temp. Setting":
                    type = "Outdoor";
                    break;
            }
            Intent intent =  new Intent();
            intent.putExtra("Type", type);
            intent.putExtra("Name", name);
            NewSetting.this.getActivity().setResult(0, intent);
            NewSetting.this.getActivity().finish();

            }
        });

        return root;
    }
}
