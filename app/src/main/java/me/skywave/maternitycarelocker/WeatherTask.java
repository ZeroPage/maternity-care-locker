package me.skywave.maternitycarelocker;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WeatherTask extends AsyncTask<Double, Void, String> {
    private final String key = "1562dbd32d37e5d8fc6ec562d8ab24a2";

    private OnTaskCompleted listener;

    public WeatherTask(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Double... doubles) {
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?" + "&appid=" + key + "&lat=" + doubles[0] + "&lon=" + doubles[1]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(false);

            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("LK-LOCK", "onPostExecute: " + s);
        listener.onTaskCompleted(s);
    }
}
