package me.skywave.maternitycarelocker.locker.core;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.LinkedList;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.controller.LockerCareController;
import me.skywave.maternitycarelocker.locker.controller.LockerUnlockController;
import me.skywave.maternitycarelocker.locker.controller.LockerWidgetController;

public class LockerDialog {
    private static Dialog CURRENT_DIALOG = null;
    private static LockerWidgetController CURRENT_WIDGET_CONTROLLER = null;
    private static LockerUnlockController CURRENT_UNLOCK_CONTROLLER = null;
    private static LockerCareController CURRENT_CARE_CONTROLLER = null;
    private static OnUnlockListener CURRENT_LISTENER = null;
    private static CustomViewPager CURRENT_VIEWPAGER = null;

    private final static int PAGE_WIDGET = 1;
    private final static int PAGE_UNLOCK = 0;
    private final static int PAGE_CARE = 2;

    public static void show(Context context) {
        if (CURRENT_DIALOG != null) {
            requestWidget();
            CURRENT_WIDGET_CONTROLLER.update();
            CURRENT_UNLOCK_CONTROLLER.update();
            CURRENT_CARE_CONTROLLER.update();

            return;
        }

        CURRENT_DIALOG = new Dialog(context, R.style.LockerDialog);
        prepareFullscreen(CURRENT_DIALOG);

        LockerPagerAdapter adapter = preparePagerAdapter(context);
        View rootView = prepareLockerView(context, adapter, PAGE_WIDGET);

        CURRENT_DIALOG.setContentView(rootView);
        CURRENT_DIALOG.show();
    }

    public static void requestUnlock(OnUnlockListener listener) {
        if (CURRENT_DIALOG == null) {
            return;
        }

        setUnlockListener(listener);
        CURRENT_VIEWPAGER.setCurrentItem(PAGE_UNLOCK, true);
    }

    public static void requestWidget() {
        if (CURRENT_DIALOG == null) {
            return;
        }

        CURRENT_VIEWPAGER.setCurrentItem(PAGE_WIDGET, true);
    }


    public static void unlock() {
        if (CURRENT_DIALOG == null) {
            return;
        }

        CURRENT_DIALOG.dismiss();
        CURRENT_DIALOG = null;

        if (CURRENT_LISTENER != null) {
            CURRENT_LISTENER.onUnlock();
            CURRENT_LISTENER = null;
        }
    }


    public static void setCallStatus(int callStatus, String caller) {
        if (CURRENT_DIALOG == null) {
            return;
        }

        CURRENT_WIDGET_CONTROLLER.setCallStatus(callStatus, caller);
    }

    private static void setUnlockListener(OnUnlockListener listener) {
        CURRENT_LISTENER = listener;
        CURRENT_UNLOCK_CONTROLLER.setUnlockActionName(listener != null ? listener.getActionName() : "");
    }

    private static LockerPagerAdapter preparePagerAdapter(Context context) {
        CURRENT_WIDGET_CONTROLLER = new LockerWidgetController(context);
        CURRENT_UNLOCK_CONTROLLER = new LockerUnlockController(context);
        CURRENT_CARE_CONTROLLER = new LockerCareController(context);

        LockerPagerAdapter adapter = new LockerPagerAdapter();
        adapter.addView(CURRENT_UNLOCK_CONTROLLER.getView());
        adapter.addView(CURRENT_WIDGET_CONTROLLER.getView());
        adapter.addView(CURRENT_CARE_CONTROLLER.getView());

        return adapter;
    }

    private static View prepareLockerView(Context context, PagerAdapter adapter, int initialIndex) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_locker, null);

        CURRENT_VIEWPAGER = (CustomViewPager) view.findViewById(R.id.viewpager);
        CURRENT_VIEWPAGER.setAdapter(adapter);
        CURRENT_VIEWPAGER.setCurrentItem(initialIndex);
        CURRENT_VIEWPAGER.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == PAGE_WIDGET) {
                    setUnlockListener(null);
                    CURRENT_VIEWPAGER.setPagingEnabled(true);
                } else if (position == PAGE_UNLOCK) {
                    CURRENT_VIEWPAGER.setPagingEnabled(false);
                }
            }
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(CURRENT_VIEWPAGER);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String pathString = preferences.getString("background_picker", "");

        if(!pathString.equals("")) {
//            Uri selectedImage = Uri.parse(pathString);
//
//            String selectedImagePath;
//
//            //MEDIA GALLERY
//            selectedImagePath = ImageFilePath.getPath(context, selectedImage);
//            Log.i("Image File Path", ""+selectedImagePath);

            Resources res = context.getResources();
            Bitmap bitmap = BitmapFactory.decodeFile(pathString);
            Log.d("LK-LOCK", "filePath: " + pathString);
            BitmapDrawable bd = new BitmapDrawable(res, bitmap);
            linearLayout.setBackground(bd);
        } else {
//            linearLayout.setBackground();
        }

        return view;
    }

    private static void prepareFullscreen(Dialog dialog) {
        int fullscreen = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LOW_PROFILE;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fullscreen |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        final View decorView = dialog.getWindow().getDecorView();
        final int finalFullscreen = fullscreen;
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                decorView.setSystemUiVisibility(finalFullscreen);
            }
        });

        decorView.setSystemUiVisibility(fullscreen);

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LockerDialog.CURRENT_DIALOG = null;
            }
        });
    }

    private static class LockerPagerAdapter extends PagerAdapter {
        private LinkedList<View> list = new LinkedList<>();

        public void addView(View view) {
            list.add(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }
    }

    public interface OnUnlockListener {
        void onUnlock();
        String getActionName();
    }
}
