package com.example.jesse.final_project;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Justin on 2017-04-05.
 */

public class OutsideTemperature extends Fragment {

    ProgressBar pBar;
    HttpURLConnection conn;
    InputStream in;
    XmlPullParser parser;
    URL url;
    String icon;
    Bitmap weatherPic;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.weather_setting, container, false);

        pBar = (ProgressBar) root.findViewById(R.id.progressBar);

        pBar.setVisibility(View.INVISIBLE);

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
            }

            try {
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    if (name.equals("temperature")) {
                        minTemperature = parser.getAttributeValue(null, "min");
                        this.publishProgress(25);
                        maxTemperature = parser.getAttributeValue(null, "max");
                        this.publishProgress(50);
                        currentTemperature = parser.getAttributeValue(null, "value");
                        this.publishProgress(75);
                    }
                    if (name.equals("wheather")) {
                        icon = parser.getAttributeValue(null, "icon");
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
            String bitMapURL = "http://openweathermap.org/img/w/" + icon + ".png";

            if (fileExistance(icon + ".png")) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(icon);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                weatherPic = BitmapFactory.decodeStream(fis);
            } else {
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

        @Override
        protected void onPostExecute(String result) {
            TextView minText, maxText, currentText;
            ImageView img;

            minText = (TextView) root.findViewById(R.id.minTemp);
            maxText = (TextView) root.findViewById(R.id.maxTemp);
            currentText = (TextView) root.findViewById(R.id.currentTemp);
            img = (ImageView) root.findViewById(R.id.wImg);

            minText.setText(minTemperature);
            maxText.setText(maxTemperature);
            currentText.setText(currentTemperature);
            img.setImageBitmap(weatherPic);

            Toast.makeText(getActivity(), "Loading Complete", Toast.LENGTH_LONG).show();

        }

        public boolean fileExistance(String fname) {
            File file = getActivity().getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

    }


    }
