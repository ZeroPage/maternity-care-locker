package me.skywave.maternitycarelocker.locker;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.everything.providers.android.calendar.Calendar;
import me.everything.providers.android.calendar.CalendarProvider;
import me.everything.providers.android.calendar.Event;

public class CalendarEventController {

    private java.util.Calendar today;
    private List<EventVO> eventsFromToday = new ArrayList<>();

    public CalendarEventController(Context context) {
        CalendarProvider calendarProvider = new CalendarProvider(context);
        today = java.util.Calendar.getInstance();
        List<Calendar> calendarList = calendarProvider.getCalendars().getList();
        List<Event> events = new ArrayList<>();
        for (Calendar calendar : calendarList) {
            if (calendar.accountType.equals("com.google")) {
                events.addAll(calendarProvider.getEvents(calendar.id).getList());
            }
        }
        eventsFromToday = getEventList(events);
        Collections.sort(eventsFromToday, new Comparator<EventVO>() {
            @Override
            public int compare(EventVO left, EventVO right) {
                return left.getDate().compareTo(right.getDate());
            }
        });
    }

    public List<EventVO> getRecentEvents(int num) {
        return eventsFromToday.subList(0, num);
    }

    private List<EventVO> getEventList (List<Event> events) {
        List<EventVO> eventDates = new ArrayList<>();

        for (Event event : events) {
            java.util.Calendar eventDate = java.util.Calendar.getInstance();
            eventDate.setTimeInMillis(event.dTStart);
            Duration duration = new Duration(event.rRule);

            if (duration.freq != null) {
                if (duration.freq.equals("YEARLY")) {
                    while (today.after(eventDate)) {
                        eventDate.add(java.util.Calendar.YEAR, duration.cycle);
                    }
                }
                else if (duration.freq.equals("MONTHLY")) {
                    while (today.after(eventDate)) {
                        eventDate.add(java.util.Calendar.MONTH, duration.cycle);
                    }
                }
                else if (duration.freq.equals("DAILY")) {
                    while (today.after(eventDate)) {
                        eventDate.add(java.util.Calendar.DAY_OF_YEAR, duration.cycle);
                    }
                }
            }
            else if (today.after(eventDate)) {
                continue;
            }
            eventDates.add(new EventVO(event.title, eventDate));
        }
        return eventDates;
    }

    private class Duration {
        String freq;
        int cycle;

        private Duration(String duration) {
            if (duration != null) {
                String[] split = duration.split(";");
                for (String s : split) {
                    if (s.contains("FREQ")) {
                        freq = s.replace("FREQ=", "");
                    }
                    if (s.contains("INTERVAL")) {
                        cycle = Integer.parseInt(s.replace("INTERVAL=", ""));
                    }
                }
            }
            else {
                freq = null;
                cycle = 0;
            }
        }
    }
}
