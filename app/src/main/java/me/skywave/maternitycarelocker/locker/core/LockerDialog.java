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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.controller.LockerInfoController;
import me.skywave.maternitycarelocker.locker.controller.LockerTimerController;
import me.skywave.maternitycarelocker.locker.controller.LockerUnlockController;
import me.skywave.maternitycarelocker.locker.controller.LockerWidgetController;
import me.skywave.maternitycarelocker.locker.model.InfoManager;
import me.skywave.maternitycarelocker.locker.view.CustomViewPager;

public class LockerDialog {
    private static String CURRENT_PATTERN_HASH = "";

    private static Dialog CURRENT_DIALOG = null;
    private static LockerWidgetController CURRENT_WIDGET_CONTROLLER = null;
    private static LockerUnlockController CURRENT_UNLOCK_CONTROLLER = null;
    private static LockerInfoController CURRENT_INFO_CONTROLLER = null;
    private static LockerTimerController CURRENT_TIMER_CONTROLLER = null;

    private static OnUnlockListener CURRENT_LISTENER = null;
    private static CustomViewPager CURRENT_VIEWPAGER = null;

    private final static int PAGE_WIDGET = 1;
    private final static int PAGE_UNLOCK = 0;

    public static void show(Context context) {
        if (CURRENT_DIALOG != null) {
            requestWidget();
            CURRENT_WIDGET_CONTROLLER.update();
            CURRENT_UNLOCK_CONTROLLER.update();
            CURRENT_INFO_CONTROLLER.update();
            CURRENT_TIMER_CONTROLLER.update();

            return;
        }

        CURRENT_PATTERN_HASH = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_pattern_hash), "");

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

        if (CURRENT_PATTERN_HASH.isEmpty()) {
            unlock();
        } else {
            CURRENT_VIEWPAGER.setCurrentItem(PAGE_UNLOCK, true);
        }
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
        CURRENT_UNLOCK_CONTROLLER = new LockerUnlockController(context, CURRENT_PATTERN_HASH);
        CURRENT_INFO_CONTROLLER = new LockerInfoController(context);
        CURRENT_TIMER_CONTROLLER = new LockerTimerController(context);

        LockerPagerAdapter adapter = new LockerPagerAdapter();
        adapter.addView(CURRENT_UNLOCK_CONTROLLER.getView(), CURRENT_UNLOCK_CONTROLLER.getTitle());
        adapter.addView(CURRENT_WIDGET_CONTROLLER.getView(), CURRENT_WIDGET_CONTROLLER.getTitle());
        if (!new InfoManager(context).getInfoVO().getPregnancyDate().isEmpty()) {
            adapter.addView(CURRENT_TIMER_CONTROLLER.getView(), CURRENT_TIMER_CONTROLLER.getTitle());
        }
        if (!new InfoManager(context).getInfoVO().isEmpty()) {
            adapter.addView(CURRENT_INFO_CONTROLLER.getView(), CURRENT_INFO_CONTROLLER.getTitle());
        }

        return adapter;
    }

    private static View prepareLockerView(Context context, final PagerAdapter adapter, int initialIndex) {
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_locker, null);

        CURRENT_VIEWPAGER = (CustomViewPager) view.findViewById(R.id.viewpager);
        CURRENT_VIEWPAGER.setAdapter(adapter);
        CURRENT_VIEWPAGER.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == PAGE_WIDGET) {
                    setUnlockListener(null);
                    CURRENT_VIEWPAGER.setPagingEnabled(true);
                } else if (position == PAGE_UNLOCK) {
                    if (CURRENT_PATTERN_HASH.isEmpty()) {
                        unlock();
                    } else {
                        CURRENT_VIEWPAGER.setPagingEnabled(false);
                    }
                }

                TextView left = (TextView) view.findViewById(R.id.text_nav_left);
                TextView right = (TextView) view.findViewById(R.id.text_nav_right);

                String[] leftTitles = new String[position];
                String[] rightTitles = new String[adapter.getCount() - (position + 1)];

                for (int i = 0; i < adapter.getCount(); i++) {
                    if (i < position) {
                        leftTitles[i] = adapter.getPageTitle(i).toString();
                    } else if (i > position) {
                        rightTitles[i - (position + 1)] = adapter.getPageTitle(i).toString();
                    }
                }

                left.setText(TextUtils.join(" / ", leftTitles));
                right.setText(TextUtils.join(" / ", rightTitles));
            }
        });

        CURRENT_VIEWPAGER.setCurrentItem(initialIndex);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(CURRENT_VIEWPAGER);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String pathString = preferences.getString(context.getResources().getString(R.string.pref_background_picker), "");

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
        private LinkedList<View> viewList = new LinkedList<>();
        private LinkedList<String> viewTitle = new LinkedList<>();

        public void addView(View view, String title) {
            viewList.add(view);
            viewTitle.add(title);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return viewTitle.get(position);
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
