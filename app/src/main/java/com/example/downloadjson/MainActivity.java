package com.example.downloadjson;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCityName;
    private TextView textViewWeather;
    private Button button;
    private String url;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.hide();
        }
        editTextCityName = findViewById(R.id.editTextCity);
        textViewWeather = findViewById(R.id.textViewWeather);
        button = findViewById(R.id.buttonGetWeather);
        }

    public void onClickDownload(View view) {
        city = editTextCityName.getText().toString().trim();
        url = String.format("https://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&appid={API KEY}"); //openweathermap API KEY NEEDED HERE
        DownloadJSONTask task = new DownloadJSONTask();
        String wrong =  getResources().getString(R.string.wrong_name);
        try {
            String got = task.execute(url).get();
            if (got.equals("wrong")) {
                textViewWeather.setText(wrong);
            } else {
                textViewWeather.setText(got);
                editTextCityName.setText("");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class DownloadJSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inReader = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(inReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                JSONObject json = new JSONObject(String.valueOf(result));
                String city = json.getString("name");
                String temp = json.getJSONObject("main").getString("temp");
                String feels = json.getJSONObject("main").getString("feels_like");
                String weather = json.getJSONArray("weather").getJSONObject(0).getString("description");
                String windSpeed = json.getJSONObject("wind").getString("speed");
                String weatherDesc = String.format(city+"\nWeather: "+weather+"\nTemperature: "+temp+" \u00B0C\nFeels like: "+feels+" \u00B0C"+"\nWind: "+windSpeed+" m/s");

                Log.i("MyResult", city);
                Log.i("MyResult", temp);
                Log.i("MyResult", weather);
                Log.i("MyResult", weatherDesc);
                return weatherDesc;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("Er", "error in doInBackground1");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Er", "error in doInBackground2");
                return "wrong";
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("Er", "JSONException");
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}
