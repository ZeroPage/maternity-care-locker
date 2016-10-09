package me.skywave.maternitycarelocker.locker.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Instances;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.skywave.maternitycarelocker.locker.model.EventVO;

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
            eventsFromToday.add(new EventVO(cInstance.getString(0), eventDate));
        }
    }

    public List<EventVO> getRecentEvents(int num) {
        return eventsFromToday.subList(0, num);
    }

    public EventVO getRecentEvent() {
        if (eventsFromToday.isEmpty()) {
            return new EventVO("NO EVENT", Calendar.getInstance());
        }

        return eventsFromToday.get(0);
    }

}
