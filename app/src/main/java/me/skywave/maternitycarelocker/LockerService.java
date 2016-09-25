package me.skywave.maternitycarelocker;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcel;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class LockerService extends IntentService {
    private LockerReceiver receiver = null;
    private TelephonyManager telephonyManager = null;
    private PhoneStateListener psListener = null;

    private final static int NOTI_ID = 7294621;

    public LockerService() {
        super("LockerService");
    }

    @Override
    public void onCreate() {
        receiver = new LockerReceiver();
        Log.e("test", "create");

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        registerReceiver(receiver, filter);

        Notification notification = new Notification();
        startForeground(NOTI_ID, notification);


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        psListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                Log.d("skywave", String.valueOf(state));
            }
        };

        telephonyManager.listen(psListener, PhoneStateListener.LISTEN_CALL_STATE);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e("test", "destroy");

        unregisterReceiver(receiver);
        stopForeground(true);
        telephonyManager.listen(psListener, 0);

        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignore) {

            }
        }
    }
}
