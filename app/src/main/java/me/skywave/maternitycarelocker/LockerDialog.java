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
    private static boolean running = false;
    private static Dialog dialog = null;

    public static void show(Context context) {
        if (running) {
            return;
        }

        dialog = new Dialog(context, R.style.LockerDialog);

        prepareFullscreen(dialog);
        prepareContents(context, dialog);

        running = true;
        dialog.show();
    }

    public static void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private static void prepareContents(Context context, final Dialog dialog) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.locker_dialog, null);

        prepareUnlock(view);
        prepareFavorite(view, context);
        prepareWeather(view, context);
        prepareTypeFaces(view, "NotoSansKR-Light.otf", context);

        dialog.setContentView(view);
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
                running = false;
                LockerDialog.dialog = null;
            }
        });
    }
}