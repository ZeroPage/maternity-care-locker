package me.skywave.maternitycarelocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;
import java.util.TreeSet;

public class LockerReceiver extends BroadcastReceiver{
    private final static Set<String> LISTENING_ACTIONS;
    private final static Set<String> LOCK_ACTIONS;

    static {
        LOCK_ACTIONS = new TreeSet<>();
        LOCK_ACTIONS.add(Intent.ACTION_SCREEN_OFF);
        LOCK_ACTIONS.add(Intent.ACTION_SCREEN_ON);
        LOCK_ACTIONS.add(Intent.ACTION_BOOT_COMPLETED);

        LISTENING_ACTIONS = new TreeSet<>();
        LISTENING_ACTIONS.addAll(LOCK_ACTIONS);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d("skywave", action);

        if (LISTENING_ACTIONS.contains(action)) {
            LockerDialog.show(context);
        } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            Toast.makeText(context, intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER), Toast.LENGTH_SHORT).show();

            setResultData(null);
        }

    }
}