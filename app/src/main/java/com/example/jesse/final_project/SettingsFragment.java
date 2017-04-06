package com.example.jesse.final_project;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

    long id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_setting_details, container, false);

        Bundle b = getArguments();

        String settingName = b.getString("value");
        id = b.getLong("id");

        TextView m = (TextView) root.findViewById(R.id.settingName);
        TextView i = (TextView) root.findViewById(R.id.idValue);

        m.setText(""+settingName);
        i.setText("ID = "+id);
/*
        Button dButton = (Button) root.findViewById(R.id.deleteButton);
        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hS == null){


                    Intent intent =  new Intent();
                    intent.putExtra("id", id);
                    SettingsFragment.this.getActivity().setResult(1, intent);
                    SettingsFragment.this.getActivity().finish();
                }else{
                    //HouseSettings.deleteId(id);
                }
            }
        });
*/
        return root;
    }
}

