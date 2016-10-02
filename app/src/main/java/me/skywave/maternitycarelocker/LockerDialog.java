package me.skywave.maternitycarelocker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.LinkedList;

public class LockerDialog {
    private static Dialog CURRENT_DIALOG = null;
    private static LockerWidgetController CURRENT_WIDGET_CONTROLLER = null;
    private static LockerUnlockController CURRENT_UNLOCK_CONTROLLER = null;
    private static OnUnlockListener CURRENT_LISTENER = null;
    private static ViewPager CURRENT_VIEWPAGER;

    private final static int PAGE_WIDGET = 1;
    private final static int PAGE_UNLOCK = 0;

    public static void show(Context context) {
        if (CURRENT_DIALOG != null) {
            return;
        }

        CURRENT_WIDGET_CONTROLLER = new LockerWidgetController(context);
        CURRENT_UNLOCK_CONTROLLER = new LockerUnlockController(context);
        CURRENT_DIALOG = new Dialog(context, R.style.LockerDialog);
        prepareFullscreen(CURRENT_DIALOG);

        LockerPagerAdapter adapter = new LockerPagerAdapter();
        adapter.addView(CURRENT_UNLOCK_CONTROLLER.getView());
        adapter.addView(CURRENT_WIDGET_CONTROLLER.getView());

        CURRENT_DIALOG.setContentView(prepareLockerView(context, adapter, PAGE_WIDGET));
        CURRENT_DIALOG.show();
    }

    public static void requestUnlock(OnUnlockListener listener) {
        if (CURRENT_DIALOG == null) {
            return;
        }

        setUnlockListener(listener);
        CURRENT_VIEWPAGER.setCurrentItem(PAGE_UNLOCK, true);
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

    private static View prepareLockerView(Context context, PagerAdapter adapter, int initialIndex) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_locker, null);

        CURRENT_VIEWPAGER = (ViewPager) view.findViewById(R.id.viewpager);
        CURRENT_VIEWPAGER.setAdapter(adapter);
        CURRENT_VIEWPAGER.setCurrentItem(initialIndex);
        CURRENT_VIEWPAGER.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == PAGE_WIDGET) {
                    setUnlockListener(null);
                }
            }
        });

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(CURRENT_VIEWPAGER);

        return view;
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
