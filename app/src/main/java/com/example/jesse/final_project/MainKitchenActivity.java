package com.example.jesse.final_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainKitchenActivity extends AppCompatActivity {

    protected EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_kitchen);

        et = (EditText) findViewById(R.id.editText);
        et.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(v.getId() == R.id.editText){
                    et.setText("");
                }
            }
        });
    }
}
