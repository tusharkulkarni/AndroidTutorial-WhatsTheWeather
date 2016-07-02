package com.tushar.whats_the_weather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    public EditText cityName;
    public TextView resultText;

    public void findWeather(View view){
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
        Log.i("cityName : ", cityName.getText().toString());
        DownloadTask task = new DownloadTask();
        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString());
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=2e7680315b3ac9b11480eb70e2ed1a2e");
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            URL url;
            HttpURLConnection connection = null;
            StringBuilder sb = new StringBuilder();
            try{
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line="";
                while(null != (line = br.readLine()) ){
                    sb.append(line);
                }

            }catch(Exception e){
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);
                StringBuilder message = new StringBuilder();
                for(int i =0; i<arr.length();i++){
                    JSONObject jsonObj = arr.getJSONObject(i);
                    String main = "";
                    String description = "";
                    main = jsonObj.getString("main");
                    description = jsonObj.getString("description");
                    if("" != main && description != ""){
                        message.append(main).append(" : ").append(description).append("\r\n");
                    }
                }
                if(message.length()>0){
                    resultText.setText(message.toString());
                }else{
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                }
            }catch(Exception e){
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        cityName = (EditText)findViewById(R.id.editText);
        resultText = (TextView)findViewById(R.id.infoText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
