package com.example.jesse.final_project;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

public class TemperatureSettings extends Fragment {

Button setTempButton, setTimeButton, delete, close;
NumberPicker tempSet;
View root;
int temp, i;
TimePicker timeSet;
String time, name;
long id;
ArrayList<String> temps, tempList;
ListView tempSched;
tempAdapter tAdpater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.temp_setting, container, false);

        tAdpater = new tempAdapter(getActivity());

        setTempButton = (Button) root.findViewById(R.id.setTempNow);
        setTimeButton = (Button) root.findViewById(R.id.setTempTime);
        tempSet = (NumberPicker) root.findViewById(R.id.tempPicker);
        timeSet = (TimePicker) root.findViewById(R.id.timePicker);
        delete = (Button) root.findViewById(R.id.tempDelete);
        close = (Button) root.findViewById(R.id.tempClose);
        tempSched = (ListView) root.findViewById(R.id.tempList);
        tempList = new ArrayList<String>();
        temps = new ArrayList<String>();

        tempSched.setAdapter(tAdpater);
        timeSet.setIs24HourView(true);

        tempSet.setMaxValue(35);
        tempSet.setMinValue(0);

        Bundle b = getArguments();

        id = b.getLong("id");
        name = b.getString("Name");

        temps = b.getStringArrayList("values");

        if (temps != null) {
            for (i = 0; i < temps.size(); i++) {
                String s = temps.get(i) + " -> " + temps.get(i + 1);
                i++;
                tempList.add(s);
            }
        }


        setTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = tempSet.getValue();
                Toast.makeText(getActivity(), "Current Temperature "+temp, Toast.LENGTH_LONG).show();
            }
        });

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = tempSet.getValue();
                time = String.format("%02d:%02d", timeSet.getHour(), timeSet.getMinute());
                String s = time+" -> "+temp;
                tempList.add(s);
                tAdpater.notifyDataSetChanged();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent();
                intent.putExtra("id", id);
                TemperatureSettings.this.getActivity().setResult(2, intent);
                TemperatureSettings.this.getActivity().finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent();
                intent.putExtra("Name", name);
                intent.putExtra("Current", temp);
                intent.putExtra("Temps", tempList);
                TemperatureSettings.this.getActivity().setResult(1, intent);
                TemperatureSettings.this.getActivity().finish();
            }
        });
        return root;


    }

    private class tempAdapter extends ArrayAdapter<String> {
        public tempAdapter(Context context) {
            super(context, 0);
        }

        public int getCount() {
            return tempList.size();
        }

        public String getItem(int position) {
            return tempList.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View result = null;
            result = inflater.inflate(R.layout.setting, null);
            TextView message = (TextView) result.findViewById(R.id.setting_text);
            message.setText(getItem(position));
            return result;


        }
    }

}
