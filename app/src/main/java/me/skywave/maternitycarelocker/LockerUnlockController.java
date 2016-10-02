package me.skywave.maternitycarelocker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LockerUnlockController {
    private View currentView;
    private TextView unlockActionText;

    public LockerUnlockController(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        currentView = inflater.inflate(R.layout.view_unlock, null);

        Button unlockButton = (Button) currentView.findViewById(R.id.button_unlock);
        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockerDialog.unlock();
            }
        });

        unlockActionText = (TextView) currentView.findViewById(R.id.text_unlock_action);
    }

    public View getView() {
        return currentView;
    }

    public void setUnlockActionName(String actionName) {
        unlockActionText.setText(actionName);
    }

}
