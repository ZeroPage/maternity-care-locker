package me.skywave.maternitycarelocker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.skywave.maternitycarelocker.VO.WeatherVO;

public class LockerActivity extends AppCompatActivity implements OnTaskCompleted {
    private TextView weatherTextView;
    private TextClock textClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker);
        Intent intent = new Intent(this, AppSelectActivity.class);
        startActivity(intent);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        weatherTextView = (TextView) findViewById(R.id.weatherText);
        textClock = (TextClock) findViewById(R.id.textClock);
        initTypeFace();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWeather();
        setFavorite();
    }

    private void initTypeFace() {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(textClock);
        textViews.add(weatherTextView);

        setTypefaces(textViews);
    }

    private void setTypefaces(ArrayList<TextView> textViews, String fontName) {
        Typeface type = Typeface.createFromAsset(this.getAssets(), fontName);
        for (TextView textView : textViews) {
            textView.setTypeface(type);
        }
    }

    private void setTypefaces(ArrayList<TextView> textViews) {
        setTypefaces(textViews, "NotoSansKR-Light.otf");
    }


    private void setWeather() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled) {
            weatherTextView.setText("GPS 연결 없음.");
        } else if (!isNetworkEnabled) {
            weatherTextView.setText("네트워크 연결 없음.");
        } else {
            String locationProvider = LocationManager.GPS_PROVIDER;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            if (lastKnownLocation != null) {
                weatherTextView.setText("로드 중");
                double lon = lastKnownLocation.getLongitude();
                double lat = lastKnownLocation.getLatitude();
                Log.d("LK-LOCK", "longtitude=" + lon + ", latitude=" + lat);
                ArrayList<Double> location = new ArrayList<>();
                location.add(lat);
                location.add(lon);
                new WeatherTask(this).execute(location);

            } else {
                Log.d("LK-LOCK", "lastKnownLocation is null");
                weatherTextView.setText("실패");
            }
        }
    }

    private void setFavorite() {
        ImageView imageView1 = (ImageView) findViewById(R.id.imageView2);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView3);

        PackageManager pm = getPackageManager();
        FavoriteManager favorite = new FavoriteManager(this);

        final List<String> packages = favorite.getFavoritePackageNames();

        if (packages.isEmpty()) {
            imageView1.setVisibility(View.INVISIBLE);
            imageView2.setVisibility(View.INVISIBLE);
        }
        try {
            ApplicationInfo ai;
            if(packages.size() >= 1) {
                imageView1.setVisibility(View.VISIBLE);
                ai = pm.getApplicationInfo(packages.get(0), 0);
                imageView1.setImageDrawable(ai.loadIcon(pm));
                imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setPackage(packages.get(0));
                        intent.setAction(Intent.ACTION_MAIN);
                        startActivity(intent);
                    }
                });
            }
            if(packages.size() == 2) {
                imageView2.setVisibility(View.VISIBLE);
                ai = pm.getApplicationInfo(packages.get(1), 0);
                imageView2.setImageDrawable(ai.loadIcon(pm));
                imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setPackage(packages.get(1));
                        intent.setAction(Intent.ACTION_MAIN);
                        startActivity(intent);
                    }
                });
            }
            else {
                imageView2.setVisibility(View.INVISIBLE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskCompleted(String s) {
        WeatherVO weatherVO = new WeatherVO();

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject main = jsonObject.getJSONObject("main");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);

            weatherVO.setTemperature(main.getString("temp"));
            weatherVO.setIcon(weather.getString("icon"));

            double temperature = Double.valueOf(weatherVO.getTemperature());
            temperature -= 273.1500;
            temperature = Double.parseDouble(String.format("%.1f",temperature));
            String weatherText = temperature + "도";
            weatherTextView.setText(weatherText);
            Drawable weatherIcon = getResources().getDrawable( weatherVO.getIcon(), this.getTheme() );
            weatherIcon.setBounds(0, 0, weatherTextView.getMeasuredHeight(), weatherTextView.getMeasuredHeight());
            weatherTextView.setCompoundDrawables(weatherIcon, null, null, null);

        } catch(Exception e) {
            e.printStackTrace();

        }
    }
}
