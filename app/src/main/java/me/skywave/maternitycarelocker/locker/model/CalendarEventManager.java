package me.skywave.maternitycarelocker.locker.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Instances;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CalendarEventManager {

    private List<EventVO> eventsFromToday = new ArrayList<>();

    private static final String[] INSTANCE_PROJECTION = new String[] {
            Instances.TITLE,
            Instances.BEGIN
    };

    public CalendarEventManager(Context context) {
        Calendar today = Calendar.getInstance();
        Calendar dtEnd = Calendar.getInstance();
        dtEnd.add(Calendar.MONTH, 1);
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cInstance = Instances.query(contentResolver, INSTANCE_PROJECTION, today.getTimeInMillis(), dtEnd.getTimeInMillis());
        while (cInstance.moveToNext()) {
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTimeInMillis(cInstance.getLong(1));
            if (eventDate.before(today))
                continue;
            today.set(Calendar.HOUR_OF_DAY, 0);
            long days = TimeUnit.MILLISECONDS.toDays(eventDate.getTimeInMillis()-today.getTimeInMillis());
            eventsFromToday.add(new EventVO(cInstance.getString(0), days));
        }
    }

    public List<EventVO> getRecentEvents(int num) {
        return eventsFromToday.subList(0, num);
    }

    public EventVO getRecentEvent() {
        if (eventsFromToday.isEmpty()) {
            return new EventVO("NO EVENT", 0);
        }

        return eventsFromToday.get(0);
    }

}
