package me.skywave.maternitycarelocker.companion.preference.profile;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePreference extends DialogPreference {
    private DatePicker datePicker;

    public DatePreference(Context context) {
        this(context, null);
    }

    public DatePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DatePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);


        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setSummary(getPersistedString(""));
    }

    @Override
    protected View onCreateDialogView() {
        datePicker = new DatePicker(getContext());
        return datePicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        String oldValue = getPersistedString("");

        if (oldValue != null && !oldValue.isEmpty()) {
            try {
                Calendar date = Calendar.getInstance();
                date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(oldValue));
                datePicker.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        if (positiveResult) {
            Calendar now = Calendar.getInstance();
            Calendar selected = Calendar.getInstance();

            selected.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

            if (selected.before(now)) {

                String newValue = String.format("%d-%02d-%02d",
                        datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth()
                );

                setValue(newValue);
            } else {
                setValue("");
            }
        }
        super.onDialogClosed(positiveResult);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            setValue(getPersistedString(""));
        } else {
            setValue("");
        }
    }

    public void setValue(String newValue) {
        persistString(newValue);
        setSummary(newValue);

        notifyChanged();
        callChangeListener(getPersistedString(""));
    }

    public String getValue() {
        return getPersistedString("");
    }
}
