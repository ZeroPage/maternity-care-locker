package me.skywave.maternitycarelocker.companion.preference.sync;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.utils.FirebaseHelper;

public class PairPreference extends DialogPreference {
    private View root;
    private FirebaseHelper.FirebasePairing firebasePairing;

    public PairPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setDialogLayoutResource(R.layout.dialog_pairing);
        setDialogIcon(null);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setSummary(getPersistedString(""));
    }

    public PairPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PairPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PairPreference(Context context) {
        this(context, null);
    }

    @Override
    protected View onCreateDialogView() {
        Log.d("skywave", getPersistedString(":"));
        root = super.onCreateDialogView();

        final TextView pairingText = (TextView) root.findViewById(R.id.text_pairing_code);

        firebasePairing = new FirebaseHelper.FirebasePairing(new FirebaseHelper.FirebasePairing.PairingEventListener() {
            @Override
            public void onEvent() {
                pairingText.setText(firebasePairing.getPairingCode());
            }
        },
        new FirebaseHelper.FirebasePairing.PairingEventListener() {
            @Override
            public void onEvent() {
                Dialog dialog = getDialog();
                if (dialog != null) {
                    dialog.cancel();
                }

                toast("성공하였습니다.");
            }
        },
        new FirebaseHelper.FirebasePairing.PairingEventListener() {
            @Override
            public void onEvent() {
                Dialog dialog = getDialog();
                if (dialog != null) {
                    dialog.cancel();
                }

                toast("실패하였습니다.");
            }
        });

        return root;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        firebasePairing.close(!positiveResult);

        if (positiveResult) {
            EditText editText = (EditText) root.findViewById(R.id.edit_pairing_code);
            String pairingCode = editText.getText().toString();

            final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "잠시만 기다려 주세요...", "");

            FirebaseHelper.acceptPairing(pairingCode, new FirebaseHelper.AcceptPairingEventListener() {
                @Override
                public void onEvent(String requesterUid) {
                    progressDialog.dismiss();

                    if (requesterUid != null) {
                        persistString(requesterUid);
                        setSummary(requesterUid);

                        toast("성공하였습니다.");
                    } else {
                        toast("실패하였습니다.");
                    }
                }
            });
        } else {
            String accepterUid = firebasePairing.getAccepterUid();

            if (accepterUid != null) {
                persistString(accepterUid);
                setSummary(accepterUid);
            }
        }

    }

    public void toast(final String message) {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            persistString(this.getPersistedString(""));
        } else {
            persistString((String) defaultValue);
        }

        setSummary(getPersistedString(""));
    }
}
