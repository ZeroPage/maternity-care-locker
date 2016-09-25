package me.skywave.maternitycarelocker;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.skywave.maternitycarelocker.VO.WeatherVO;

public class LockerDialog {
    private static boolean RUNNING = false;

    private static Dialog CURRENT_DIALOG = null;
    private static View CURRENT_ROOT_VIEW = null;
    private static int CALL_STATUS = 0;

    public static void show(Context context) {
        if (RUNNING) {
            return;
        }

        CURRENT_DIALOG = new Dialog(context, R.style.LockerDialog);

        prepareFullscreen(CURRENT_DIALOG);
        prepareContents(context, CURRENT_DIALOG);

        RUNNING = true;
        CURRENT_DIALOG.show();
    }

    public static void dismiss() {
        if (CURRENT_DIALOG != null) {
            CURRENT_DIALOG.dismiss();
            CURRENT_DIALOG = null;
            CURRENT_ROOT_VIEW = null;
        }
    }

    public static void setCallStatus(int callStatus) {
        if (callStatus < 0 || callStatus > 2) {
            return;
        }

        CALL_STATUS = callStatus;

        if (RUNNING) {
            updateCallButtons(CURRENT_ROOT_VIEW, callStatus);
        }
    }

    private static void prepareContents(Context context, final Dialog dialog) {
        LayoutInflater inflater = LayoutInflater.from(context);
        CURRENT_ROOT_VIEW = inflater.inflate(R.layout.locker_dialog, null);

        prepareUnlock(CURRENT_ROOT_VIEW);
        prepareFavorite(CURRENT_ROOT_VIEW, context);
        prepareWeather(CURRENT_ROOT_VIEW, context);
        prepareCallButtons(CURRENT_ROOT_VIEW, context);
        prepareTypeFaces(CURRENT_ROOT_VIEW, "NotoSansKR-Light.otf", context);
        updateCallButtons(CURRENT_ROOT_VIEW, CALL_STATUS);

        dialog.setContentView(CURRENT_ROOT_VIEW);
    }

    private static void prepareUnlock(View rootView) {
        Button unlockButton = (Button) rootView.findViewById(R.id.button_unlock);
        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private static void prepareCallButtons(View rootView, final Context context) {
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

    private static void updateCallButtons(View rootView, int callState) {
        Button acceptButton = (Button) rootView.findViewById(R.id.button_call_accept);
        Button dismissButton = (Button) rootView.findViewById(R.id.button_call_dismiss);

        acceptButton.setVisibility(callState == TelephonyManager.CALL_STATE_RINGING ? View.VISIBLE : View.INVISIBLE);
        dismissButton.setVisibility((callState & (TelephonyManager.CALL_STATE_RINGING | TelephonyManager.CALL_STATE_OFFHOOK)) != 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private static void prepareTypeFaces(View view, String fontName, Context context) {
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add((TextClock) view.findViewById(R.id.textClock));
        textViews.add((TextView) view.findViewById(R.id.weatherText));

        Typeface type = Typeface.createFromAsset(context.getAssets(), fontName);

        for (TextView textView : textViews) {
            textView.setTypeface(type);
        }
    }

    private static void prepareFavorite(View rootView, final Context context) {
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
                        context.startActivity(intent);
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
                        context.startActivity(intent);
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


    private static void prepareWeather(final View rootView, final Context context) {

        final TextView weatherTextView = (TextView) rootView.findViewById(R.id.weatherText);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled) {
            weatherTextView.setText("GPS 연결 없음.");
        } else if (!isNetworkEnabled) {
            weatherTextView.setText("네트워크 연결 없음.");
        } else {
            String locationProvider = LocationManager.GPS_PROVIDER;
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

            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            if (lastKnownLocation != null) {
                weatherTextView.setText("로드 중");
                double lon = lastKnownLocation.getLongitude();
                double lat = lastKnownLocation.getLatitude();
                Log.d("LK-LOCK", "longtitude=" + lon + ", latitude=" + lat);

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
                            temperature = Double.parseDouble(String.format("%.1f",temperature));
                            String weatherText = temperature + "도";
                            weatherTextView.setText(weatherText);
                            Drawable weatherIcon = context.getResources().getDrawable( weatherVO.getIcon(), null); // fixme: can't get the theme
                            weatherIcon.setBounds(0, 0, weatherTextView.getMeasuredHeight(), weatherTextView.getMeasuredHeight());
                            weatherTextView.setCompoundDrawables(weatherIcon, null, null, null);

                        } catch(Exception e) {
                            e.printStackTrace();

                        }
                    }
                }).execute(lat, lon);

            } else {
                Log.d("LK-LOCK", "lastKnownLocation is null");
                weatherTextView.setText("실패");
            }
        }
    }

    private static void prepareFullscreen(Dialog dialog) {
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                RUNNING = false;
                LockerDialog.CURRENT_DIALOG = null;
            }
        });
    }
}
