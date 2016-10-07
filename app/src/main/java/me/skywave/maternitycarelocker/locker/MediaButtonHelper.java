package me.skywave.maternitycarelocker.locker;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;

public class MediaButtonHelper {

    // http://stackoverflow.com/questions/26924618/how-can-incoming-calls-be-answered-programmatically-in-android-5-0-lollipop
    public static void pressMediaButton(final long ms, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("skywave", (ms >= 1000 ? "longpress" : "shortpress"));

                    Runtime.getRuntime().exec("input keyevent " + (ms >= 1000 ? "--longpress " : "") +
                            Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
                } catch (IOException e) {

                    boolean headsetOn = isHeadsetOn(context);

                    if (!headsetOn) {
                        broadcastHeadsetConnected(true, context);
                    }

                    Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                            Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                    KeyEvent.KEYCODE_HEADSETHOOK));
                    context.sendOrderedBroadcast(btnDown, "android.permission.CALL_PRIVILEGED");
                    Log.d("skywave", "down");

                    try {
                        Thread.sleep(ms);
                    } catch (InterruptedException ignore) {

                    }

                    Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                            Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                    KeyEvent.KEYCODE_HEADSETHOOK));
                    context.sendOrderedBroadcast(btnUp, "android.permission.CALL_PRIVILEGED");
                    Log.d("skywave", "up");

                    if (!headsetOn) {
                        broadcastHeadsetConnected(false, context);
                    }

                }

            }
        }).start();
    }

    public static boolean isHeadsetOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        return audioManager.isWiredHeadsetOn();
    }

    private static void broadcastHeadsetConnected(boolean connected, Context context) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);

        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");

        try {
            context.sendOrderedBroadcast(i, null);
        } catch (Exception ignore) {

        }
    }
}
