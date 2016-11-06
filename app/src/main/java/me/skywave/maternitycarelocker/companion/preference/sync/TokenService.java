package me.skywave.maternitycarelocker.companion.preference.sync;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static me.skywave.maternitycarelocker.utils.FirebaseHelper.registerToken;

public class TokenService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        registerToken(FirebaseInstanceId.getInstance().getToken());
    }
}
