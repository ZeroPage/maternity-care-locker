package me.skywave.maternitycarelocker.locker.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.core.LockerDialog;
import me.skywave.maternitycarelocker.locker.model.BabyfairVO;
import me.skywave.maternitycarelocker.locker.model.CalendarEventManager;
import me.skywave.maternitycarelocker.locker.model.EventVO;
import me.skywave.maternitycarelocker.locker.model.InfoManager;
import me.skywave.maternitycarelocker.locker.model.InfoVO;
import me.skywave.maternitycarelocker.locker.model.WeatherVO;
import me.skywave.maternitycarelocker.utils.FavoriteManager;
import me.skywave.maternitycarelocker.utils.FirebaseHelper;
import me.skywave.maternitycarelocker.utils.MediaButtonUtil;
import me.skywave.maternitycarelocker.utils.RadioUtil;

public class LockerWidgetController extends LockerController implements LocationListener {
    private int callStatus = 0;
    private boolean weatherSwitch;
    private boolean calendarSwitch;
    private boolean fairSwitch;
    private boolean adviceSwitch;
    private boolean musicSwitch;

    public LockerWidgetController(Context context) {
        super(R.layout.view_widget, context);
        setSwitches();
        prepareFavorite(currentView);
        if (weatherSwitch) {
            prepareWeather(currentView);
        }
        prepareGesture();
        if (calendarSwitch) {
            prepareCalendar(currentView);
        }
        if (fairSwitch) {
            prepareFair(currentView);
        }
        prepareCallButtons(currentView);
        prepareTypeFaces();
        if (musicSwitch) {
            prepareMusicButton();
        }
        updateCallLayout(currentView, callStatus, null);
        update();
    }

    public void update() {
        setSwitches();
        if (weatherSwitch) {
            prepareWeather(currentView);
        }
        if (adviceSwitch) {
            prepareAdvice(currentView);
        }
        if (calendarSwitch) {
            prepareCalendar(currentView);
        }

        if (musicSwitch) {
            prepareMusicButton();
        }
        else {
            ToggleButton musicButton = (ToggleButton)currentView.findViewById(R.id.button_music);
            musicButton.setVisibility(View.INVISIBLE);
        }
    }

    public void setCallStatus(int callStatus, String caller) {
        if (callStatus < 0 || callStatus > 2) {
            return;
        }

        updateCallLayout(currentView, callStatus, caller);
    }

    private void prepareGesture() {
        final GestureDetector gesture = new GestureDetector(currentContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (velocityY > 500) {
                    Object service  = currentContext.getSystemService("statusbar");
                    Class<?> statusbarManager = null;
                    try {
                        statusbarManager = Class.forName("android.app.StatusBarManager");
                        Method expand = statusbarManager.getMethod("expandNotificationsPanel");
                        expand.invoke(service);
                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        currentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
    }

    private void prepareTypeFaces() {
        ArrayList<TextView> lightTexts = new ArrayList<>();
        ArrayList<TextView> regTexts = new ArrayList<>();
        lightTexts.add((TextClock) currentView.findViewById(R.id.textClock1));
        lightTexts.add((TextClock) currentView.findViewById(R.id.textClock2));
        lightTexts.add((TextView) currentView.findViewById(R.id.weatherText));
        lightTexts.add((TextView) currentView.findViewById(R.id.calendarTitleText));
        lightTexts.add((TextView) currentView.findViewById(R.id.fairText));
        lightTexts.add((TextView) currentView.findViewById(R.id.adviceText));

//        regTexts.add((TextView) currentView.findViewById(R.id.calendarDateText));

        setTypeFaces(FONT_NOTO_THIN, lightTexts);
//        setTypeFaces(FONT_NOTO_REG, regTexts);
    }


    private void prepareCallButtons(View rootView) {
        Button acceptButton = (Button) rootView.findViewById(R.id.button_call_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaButtonUtil.pressMediaButton(100, currentContext);
            }
        });

        Button dismissButton = (Button) rootView.findViewById(R.id.button_call_dismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaButtonUtil.pressMediaButton(3000, currentContext);
            }
        });
    }

    private void updateCallLayout(View rootView, int callState, String caller) {
        View adviceLayout = rootView.findViewById(R.id.layout_advice);
        View callLayout = rootView.findViewById(R.id.layout_call);

        Button acceptButton = (Button) rootView.findViewById(R.id.button_call_accept);
        Button dismissButton = (Button) rootView.findViewById(R.id.button_call_dismiss);
        TextView callerText = (TextView) rootView.findViewById(R.id.text_caller);

        Log.d("skywave", caller == null ? "" : caller);

        int callVisibility = (callState & (TelephonyManager.CALL_STATE_RINGING | TelephonyManager.CALL_STATE_OFFHOOK)) != 0 ? View.VISIBLE : View.GONE;
        int adviceVisibility = callVisibility == View.VISIBLE ? View.GONE : View.VISIBLE;

        adviceLayout.setVisibility(adviceVisibility);
        callLayout.setVisibility(callVisibility);

        if (callVisibility == View.VISIBLE) {
            int acceptVisibility = callState == TelephonyManager.CALL_STATE_RINGING ? View.VISIBLE : View.GONE;
            acceptButton.setVisibility(acceptVisibility);

            dismissButton.setVisibility(callVisibility);
            callerText.setVisibility(callVisibility);

            callerText.setText(caller != null ? caller : "");
        }
    }

    private void prepareFavorite(View rootView) {
        ImageView imageView1 = (ImageView) rootView.findViewById(R.id.imageView2);
        ImageView imageView2 = (ImageView) rootView.findViewById(R.id.imageView3);

        final PackageManager pm = currentContext.getPackageManager();
        FavoriteManager favorite = new FavoriteManager(currentContext);

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
                final String appName1 = ai.loadLabel(pm).toString();
                imageView1.setImageDrawable(ai.loadIcon(pm));
                imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LockerDialog.requestUnlock(new LockerDialog.OnUnlockListener() {
                            @Override
                            public void onUnlock() {
                                Intent intent = pm.getLaunchIntentForPackage(packages.get(0));
                                try {
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    currentContext.startActivity(intent);
                                } catch (NullPointerException e) {
                                    Toast.makeText(currentContext, "실행할 수 없는 앱입니다", Toast.LENGTH_LONG);
                                }
                            }

                            @Override
                            public String getActionName() {
                                return appName1;
                            }
                        });
                    }
                });
            }
            if (packages.size() == 2) {
                imageView2.setVisibility(View.VISIBLE);
                ai = pm.getApplicationInfo(packages.get(1), 0);
                final String appName2 = ai.loadLabel(pm).toString();
                imageView2.setImageDrawable(ai.loadIcon(pm));
                imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LockerDialog.requestUnlock(new LockerDialog.OnUnlockListener() {
                            @Override
                            public void onUnlock() {
                                Intent intent = pm.getLaunchIntentForPackage(packages.get(1));
                                try {
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    currentContext.startActivity(intent);
                                } catch (NullPointerException e) {
                                    Toast.makeText(currentContext, "실행할 수 없는 앱입니다", Toast.LENGTH_LONG);
                                }
                            }

                            @Override
                            public String getActionName() {
                                return appName2;
                            }
                        });
                    }
                });
            } else {
                imageView2.setVisibility(View.INVISIBLE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void prepareMusicButton() {
        ToggleButton button = (ToggleButton) currentView.findViewById(R.id.button_music);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RadioUtil.isPlaying()) {
                    RadioUtil.stop();
                } else {
                    RadioUtil.play();
                }

                updateMusicButton();
            }
        });
    }

    private void updateMusicButton() {
        ToggleButton button = (ToggleButton) currentView.findViewById(R.id.button_music);

        button.setChecked(RadioUtil.isPlaying());
    }

    private void prepareAdvice(View currentView) {
        TextView adviceText = (TextView) currentView.findViewById(R.id.adviceText);
        InfoManager infoManager = new InfoManager(currentContext);
        InfoVO infoVO = infoManager.getInfoVO();
        int week = (int)(infoVO.getDaysFromPragnancy()/7) + 1;
        int fileName = week / 4;

        try {
            AssetManager am = currentContext.getAssets();
            InputStream is = am.open("week" + fileName + ".json");
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            String text = jsonArray.getString((int) (Math.random() * jsonArray.length()));


            adviceText.setText(text);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("LK-LOCK", "Fail to load json");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("LK-LOCK", "Fail to parse json");
        }

    }

    private void prepareWeather(final View rootView) {
        final TextView weatherTextView = (TextView) rootView.findViewById(R.id.weatherText);

        double lat = 0, lon = 0;

        LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
        // GPS
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // Network
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(currentContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            weatherTextView.setText("--°C");

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
                            String weatherText = temperature + "°C";
                            weatherTextView.setText(weatherText);
                            Drawable weatherIcon = currentContext.getResources().getDrawable(weatherVO.getIcon(), null); // fixme: can't get the theme
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

    private void prepareCalendar(View rootView) {
        TextView calendarTitle = (TextView) rootView.findViewById(R.id.calendarTitleText);
        TextView calendarDate = (TextView) rootView.findViewById(R.id.calendarDateText);

        CalendarEventManager controller = new CalendarEventManager(currentContext);
        EventVO recentEvent = controller.getRecentEvent();
        if (recentEvent.getTitle().equals("NO EVENT")) {
            calendarTitle.setVisibility(View.INVISIBLE);
            calendarDate.setVisibility(View.INVISIBLE);
        }
        else {
            long eventDay = recentEvent.getDDay();
            calendarTitle.setText(recentEvent.getTitle());
            calendarDate.setText("D-" + eventDay);
        }
    }

    private void prepareFair(View rootView) {
        final TextView fairText = (TextView) rootView.findViewById(R.id.fairText);
        fairText.setText("");

        FirebaseHelper.requestBabyfairList(new FirebaseHelper.RequestBabyfairListEventListener() {
            @Override
            public void onEvent(List<BabyfairVO> babyfairs) {
                if (babyfairs != null) {
                    BabyfairVO babyfair = babyfairs.get(0);

                    fairText.setText(String.format("D-%s %s", babyfair.getDDay(), babyfair.getTitle()));
                }

            }
        });

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

    private void setSwitches() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(currentContext);
        weatherSwitch = pref.getBoolean("weather_switch", true);
        calendarSwitch = pref.getBoolean("calendar_switch", true);
        fairSwitch = pref.getBoolean("fair_switch", true);
        adviceSwitch = pref.getBoolean("advice_switch", true);
        musicSwitch = pref.getBoolean("music_switch", true);
    }

}
