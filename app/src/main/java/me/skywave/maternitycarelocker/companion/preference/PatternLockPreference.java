package me.skywave.maternitycarelocker.companion.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;

import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;

public class PatternLockPreference extends DialogPreference {
    private String currentValue = "";
    private String newValue;
    private Checkable checkableView;
    private PatternView patternView;
    private TextView textView;

    public PatternLockPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setDialogLayoutResource(R.layout.dialog_pattern_lock);
        setDialogIcon(null);
    }

    public PatternLockPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PatternLockPreference(Context context, AttributeSet attrs) {
        this(context, attrs, Resources.getSystem().getIdentifier("switchPreferenceStyle", "attr", "android"));
    }

    public PatternLockPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void showDialog(Bundle state) {
        if (currentValue.isEmpty()) {
            setPositiveButtonText(android.R.string.ok);
        } else {
            setPositiveButtonText(null);
        }

        setNegativeButtonText(android.R.string.cancel);

        super.showDialog(state);
    }

    @Override
    protected View onCreateDialogView() {
        View root = super.onCreateDialogView();
        patternView = (PatternView) root.findViewById(R.id.pattern_view);
        textView = (TextView) root.findViewById(R.id.text_view);

        patternView.setOnPatternListener(new PatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<PatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<PatternView.Cell> pattern) {
                newValue = PatternUtils.patternToSha1String(pattern);

                if (!currentValue.isEmpty()) {
                    if (currentValue.equals(newValue)) {
                        ((AlertDialog) getDialog()).cancel();
                        onDialogClosed(true);
                    } else {
                        patternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                    }
                } else {
                    patternView.setDisplayMode(PatternView.DisplayMode.Animate);
                }
            }
        });


        if (currentValue.isEmpty()) {
            textView.setText("새 비밀번호를 입력하세요");
        } else {
            textView.setText("기존 비밀번호를 입력하세요");
        }



        return root;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (currentValue.isEmpty()) {
                setCurrentValue(newValue);
            } else {
                setCurrentValue("");
            }
        }
    }


    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        int identifier;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            identifier = android.R.id.switch_widget;
        } else {
            identifier = Resources.getSystem().getIdentifier("switchWidget", "id", "android");
        }

        checkableView = (Checkable) view.findViewById(identifier);
        updateView();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            setCurrentValue(this.getPersistedString(""));
        } else {
            setCurrentValue((String) defaultValue);
        }
    }

    private void setCurrentValue(String value) {
        currentValue = value != null ? value : "";
        persistString(currentValue);
        updateView();
    }

    private void updateView() {
        if (checkableView != null && currentValue != null) {
            checkableView.setChecked(!currentValue.isEmpty());
        }

    }
}
