package com.example.jesse.final_project;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class OutsideTemperature extends Fragment {

    ProgressBar pBar;
    HttpURLConnection conn;
    InputStream in;
    XmlPullParser parser;
    URL url;
    String icon;
    Bitmap weatherPic;
    View root;
    String activity = "Outdoor Temp.";
    Button close, delete;
    long id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.weather_setting, container, false);

        pBar = (ProgressBar) root.findViewById(R.id.progressBar);

        pBar.setVisibility(View.INVISIBLE);

        delete = (Button) root.findViewById(R.id.outDelete);
        close = (Button) root.findViewById(R.id.outClose);

        Bundle b = getArguments();

        id = b.getLong("id");


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent();
                intent.putExtra("id", id);
                OutsideTemperature.this.getActivity().setResult(2, intent);
                OutsideTemperature.this.getActivity().finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent();
                OutsideTemperature.this.getActivity().setResult(1, intent);
                OutsideTemperature.this.getActivity().finish();
            }
        });

        return root;
    }

    public void executeQuery(){
        new ForecastQuery().execute();
    }

    class ForecastQuery extends AsyncTask<String, Integer, String> {
        String minTemperature, maxTemperature, currentTemperature;


        @Override
        protected String doInBackground(String... params) {
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=ottawa," +
                    "ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric";

            try {
                url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                in = conn.getInputStream();
                parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (SecurityException e){
                e.printStackTrace();
            }

            try {
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    if (name.equals("temperature")) {
                        minTemperature = parser.getAttributeValue(null, "min");
                        Log.i(activity, minTemperature);
                        this.publishProgress(25);
                        maxTemperature = parser.getAttributeValue(null, "max");
                        Log.i(activity, maxTemperature);
                        this.publishProgress(50);
                        currentTemperature = parser.getAttributeValue(null, "value");
                        Log.i(activity, currentTemperature);
                        this.publishProgress(75);
                    }
                    if (name.equals("weather")) {
                        icon = parser.getAttributeValue(null, "icon");
                        String bitMapURL = "http://openweathermap.org/img/w/" + icon + ".png";

                        if (fileExistance(icon + ".png")) {
                            Log.i(activity, "Attempting to read " + icon + " from disk");
                            FileInputStream fis = null;
                            try {
                                File file = getActivity().getBaseContext().getFileStreamPath(icon + ".png");
                                fis = new FileInputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            weatherPic = BitmapFactory.decodeStream(fis);
                        } else {
                            Log.i(activity, "Attempting to download " + icon);
                            weatherPic = HttpUtils.getImage(bitMapURL);
                            try {
                                FileOutputStream outputStream = getActivity().openFileOutput(icon + ".png", Context.MODE_PRIVATE);
                                weatherPic.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }



                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            pBar.setVisibility(View.VISIBLE);
            pBar.setProgress(value[0]);

        }

        @Override
        protected void onPostExecute(String result) {
            TextView minText, maxText, currentText;
            ImageView img;

            minText = (TextView) root.findViewById(R.id.minTemp);
            maxText = (TextView) root.findViewById(R.id.maxTemp);
            currentText = (TextView) root.findViewById(R.id.currentTemp);
            img = (ImageView) root.findViewById(R.id.wImg);

            minText.setText("Low: "+minTemperature);
            maxText.setText("High: "+maxTemperature);
            currentText.setText("Current: "+currentTemperature);
            img.setImageBitmap(weatherPic);

            pBar.setVisibility(View.INVISIBLE);

        }

        public boolean fileExistance(String fname) {
            File file = getActivity().getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

    }
}
