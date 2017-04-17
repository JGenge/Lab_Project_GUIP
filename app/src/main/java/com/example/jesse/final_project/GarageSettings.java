package com.example.jesse.final_project;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;

public class GarageSettings extends Fragment {

    View root;
    Button doorButton, close, delete;
    CheckBox lightCheckBox;
    Boolean openClose = false;              //True = door open, False = door close
    String doorToastMessage, lightToastMessage;
    long id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.garage_setting, container, false);

        doorButton = (Button) root.findViewById(R.id.doorButton);       //Settings value 0
        close = (Button) root.findViewById(R.id.garageClose);
        delete = (Button) root.findViewById(R.id.garageDelete);
        lightCheckBox = (CheckBox) root.findViewById(R.id.lightCheckBox);   //Settings value 1

        Bundle b = getArguments();

        id = b.getLong("id");

        ArrayList<String> values = b.getStringArrayList("Values");

        if(!values.contains(null)) {
            if (values.get(0).equals("Open")) {
                openClose = true;
            } else if (values.get(0).equals("Close")) {
                openClose = false;
            }

            if (values.get(1).equals("On")) {
                lightCheckBox.setChecked(true);
            } else if (values.get(1).equals("Off")) {
                lightCheckBox.setChecked(false);
            }
        }
        doorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(openClose == true){
                    doorToastMessage = "Closing garage door";
                    openClose = false;
                }
                else{
                    doorToastMessage = "Opening garage door";
                    lightCheckBox.setChecked(true);
                    openClose = true;
                }
                Toast.makeText(getActivity(), doorToastMessage, Toast.LENGTH_LONG).show();
            }
        });

        lightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lightToastMessage = "Light is On";
                }
                else if (!isChecked){
                    lightToastMessage = "Light is Off";
                }
                Toast.makeText(getActivity(), lightToastMessage, Toast.LENGTH_LONG).show();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(GarageSettings.this.getActivity(), HouseSettings.class);
                GarageSettings.this.getActivity().setResult(1, intent);
                GarageSettings.this.getActivity().finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent();
                intent.putExtra("id", id);
                GarageSettings.this.getActivity().setResult(2, intent);
                GarageSettings.this.getActivity().finish();
            }
        });
        return root;
    }

}
