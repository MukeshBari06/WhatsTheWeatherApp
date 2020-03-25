package com.example.jsondemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView outputView;
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... Urls) {
            String result="";
            URL url ;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(Urls[0]);
                urlConnection= (HttpURLConnection)url.openConnection();
                InputStream in =urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();

                while (data!=-1){
                    char current=(char) data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            }catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find Weather :(",Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                String mainInfojson=jsonObject.getString("main");
                JSONObject mainInfo = new JSONObject(mainInfojson);
                String temp_min=mainInfo.getString("temp_min")+"°C";
                String temp_max=mainInfo.getString("temp_max")+"°C";
                String humidity=mainInfo.getString("humidity")+"%";
                String pressure=mainInfo.getString("pressure")+" mbar";

                String windInfojson=jsonObject.getString("wind");
                JSONObject windInfo = new JSONObject(windInfojson);
                String speed=windInfo.getString("speed")+" m/sec";
                String deg=windInfo.getString("deg")+"°";

                String weatherInfo=jsonObject.getString("weather");
                //Log.i("Weather Info",weatherInfo);
                JSONArray arr=new JSONArray(weatherInfo);
                String message="";
                for(int i=0;i<arr.length();i++){
                    JSONObject part=arr.getJSONObject(i);
                    String main=part.getString("main");
                    String description=part.getString("description");
                    if(!main.equals("")&&!description.equals("")){
                        message+=main+": "+description+"\n";
                    }
                }
                if(!temp_max.equals("")&&!temp_min.equals("")&&!humidity.equals("")){
                    message+="\nTemp: (min) "+temp_min+", (max) "+temp_max+"\n"+"Humidity: "+humidity+"\n"+"Pressure: "+pressure+"\n"+"Wind: "+speed+" "+deg;
                }
                if(!message.equals("")) {
                    outputView.setText(message);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Could not find Weather :(",Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find Weather :(",Toast.LENGTH_SHORT).show();
            }

            //Log.i("JSON",s);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=(EditText) findViewById(R.id.editText);
        outputView=(TextView)findViewById(R.id.textView2);
    }

    public void getWeather(View view){
        try {
            String URL = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + URL + "&appid=b6907d289e10d714a6e88b30761fae22");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Could not find Weather :(",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
