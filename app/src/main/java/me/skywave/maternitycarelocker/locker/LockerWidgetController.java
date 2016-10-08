package me.skywave.maternitycarelocker.locker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.companion.preference.FavoriteManager;

public class LockerWidgetController implements LocationListener {
    private int callStatus = 0;
    private View currentView;

    public LockerWidgetController(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        currentView = inflater.inflate(R.layout.view_widget, null);
        prepareFavorite(currentView, context);
        if (preferences.getBoolean("weather_switch", true)) {
            prepareWeather(currentView, context);
        }
        prepareCalendar(currentView, context);
        prepareCallButtons(currentView, context);
        prepareTypeFaces(currentView, "NotoSansKR-Light.otf", context);
        updateCallButtons(currentView, callStatus, null);
    }

    public void update(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("weather_switch", true)) {
            prepareWeather(currentView, context);
        }
    }

    public View getView() {
        return currentView;
    }

    public void setCallStatus(int callStatus, String caller) {
        if (callStatus < 0 || callStatus > 2) {
            return;
        }

        updateCallButtons(currentView, callStatus, caller);
    }

    private void prepareCallButtons(View rootView, final Context context) {
        Button acceptButton = (Button) rootView.findViewById(R.id.button_call_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaButtonHelper.pressMediaButton(100, context);
            }
        });

        Button dismissButton = (Button) rootView.findViewById(R.id.button_call_dismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaButtonHelper.pressMediaButton(3000, context);
            }
        });
    }

    private void updateCallButtons(View rootView, int callState, String caller) {
        Button acceptButton = (Button) rootView.findViewById(R.id.button_call_accept);
        Button dismissButton = (Button) rootView.findViewById(R.id.button_call_dismiss);
        TextView callerText = (TextView) rootView.findViewById(R.id.text_caller);

        Log.d("skywave", caller == null ? "" : caller);

        int acceptVisibility = callState == TelephonyManager.CALL_STATE_RINGING ? View.VISIBLE : View.INVISIBLE;
        acceptButton.setVisibility(acceptVisibility);

        int dismissVisibility = (callState & (TelephonyManager.CALL_STATE_RINGING | TelephonyManager.CALL_STATE_OFFHOOK)) != 0 ? View.VISIBLE : View.INVISIBLE;
        dismissButton.setVisibility(dismissVisibility);
        callerText.setVisibility(dismissVisibility);

        callerText.setText(caller != null ? caller : "");
    }

    private void prepareTypeFaces(View view, String fontName, Context context) {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add((TextClock) view.findViewById(R.id.textClock));
        textViews.add((TextView) view.findViewById(R.id.weatherText));
        textViews.add((TextView) view.findViewById(R.id.calendarDateText));
        textViews.add((TextView) view.findViewById(R.id.calendarTitleText));

        Typeface type = Typeface.createFromAsset(context.getAssets(), fontName);

        for (TextView textView : textViews) {
            textView.setTypeface(type);
        }
    }

    private void prepareFavorite(View rootView, final Context context) {
        ImageView imageView1 = (ImageView) rootView.findViewById(R.id.imageView2);
        ImageView imageView2 = (ImageView) rootView.findViewById(R.id.imageView3);

        PackageManager pm = context.getPackageManager();
        FavoriteManager favorite = new FavoriteManager(context);

        final List<String> packages = favorite.getFavoritePackageNames();

        if (packages.isEmpty()) {
            imageView1.setVisibility(View.INVISIBLE);
            imageView2.setVisibility(View.INVISIBLE);
        }
        try {
            ApplicationInfo ai;
            if (packages.size() >= 1) {
                imageView1.setVisibility(View.VISIBLE);
                ai = pm.getApplicationInfo(packages.get(0), 0);
                imageView1.setImageDrawable(ai.loadIcon(pm));
                imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setPackage(packages.get(0));
                        intent.setAction(Intent.ACTION_MAIN);
                        context.startActivity(intent);
                    }
                });
            }
            if (packages.size() == 2) {
                imageView2.setVisibility(View.VISIBLE);
                ai = pm.getApplicationInfo(packages.get(1), 0);
                imageView2.setImageDrawable(ai.loadIcon(pm));
                imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setPackage(packages.get(1));
                        intent.setAction(Intent.ACTION_MAIN);
                        context.startActivity(intent);
                    }
                });
            } else {
                imageView2.setVisibility(View.INVISIBLE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void prepareWeather(final View rootView, final Context context) {
        final TextView weatherTextView = (TextView) rootView.findViewById(R.id.weatherText);

        double lat = 0, lon = 0;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // GPS
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // Network
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        if (!isGPSEnabled && !isNetworkEnabled) {
//            weatherTextView.setText("GPS 및 네트워크 연결 없음.");
        } else {
            weatherTextView.setText("--도");

            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10f, this);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null) {
                    // 위도, 경도
                    lat = lastKnownLocation.getLatitude();
                    lon = lastKnownLocation.getLongitude();
                } else {
                    Log.d("LK-LOCK", "network lastKnownLocation is null");
                }
            }

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10f, this);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    // 위도, 경도
                    lat = lastKnownLocation.getLatitude();
                    lon = lastKnownLocation.getLongitude();
                } else {
                    Log.d("LK-LOCK", "gps lastKnownLocation is null");
                }
            }

            Log.d("LK-LOCK", "longtitude=" + lon + ", latitude=" + lat);

            if (lon == 0 && lat == 0) {
                weatherTextView.setText("");
//                weatherTextView.setText("실패");
            } else {
                new WeatherTask(new OnTaskCompleted() {
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
                            temperature = Double.parseDouble(String.format("%.1f", temperature));
                            String weatherText = temperature + "도";
                            weatherTextView.setText(weatherText);
                            Drawable weatherIcon = context.getResources().getDrawable(weatherVO.getIcon(), null); // fixme: can't get the theme
                            weatherIcon.setBounds(0, 0, weatherTextView.getMeasuredHeight(), weatherTextView.getMeasuredHeight());
                            weatherTextView.setCompoundDrawables(weatherIcon, null, null, null);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(lat, lon);
            }
        }
    }

    private void prepareCalendar(View rootView, Context context) {
        TextView calendarTitle = (TextView) rootView.findViewById(R.id.calendarTitleText);
        TextView calendarDate = (TextView) rootView.findViewById(R.id.calendarDateText);

        CalendarEventController controller = new CalendarEventController(context);
        EventVO recentEvent = controller.getRecentEvent();
        Calendar eventDate = recentEvent.getDate();

        calendarTitle.setText("Coming Soon... " + recentEvent.getTitle());
        calendarDate.setText((eventDate.get(Calendar.MONTH)+1) + "월" + eventDate.get(Calendar.DAY_OF_MONTH) + "일");
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
