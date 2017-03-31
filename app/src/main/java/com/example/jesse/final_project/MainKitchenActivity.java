package com.example.jesse.final_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainKitchenActivity extends AppCompatActivity {

    protected EditText et;
    protected Button bt1, bt2;
    protected ListView lv;
    protected ArrayList al;
    protected ArrayAdapter aa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_kitchen);

        lv = (ListView) findViewById(R.id.listView);
        al = new ArrayList<String>();
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al);

        lv.setAdapter(aa);

        et = (EditText) findViewById(R.id.editText);

        bt1 = (Button) findViewById(R.id.button1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = et.getText().toString();
                al.add(0,item);
                aa.notifyDataSetChanged();
                et.setText("");
            }
        });

        bt2 = (Button) findViewById(R.id.button2);

        lv = (ListView) findViewById(R.id.listView);
    }
}
