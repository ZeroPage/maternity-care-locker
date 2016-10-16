package me.skywave.maternitycarelocker.locker.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.TimerVO;

public class TimerListAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<TimerVO> timerList;
    private int resource;

    public TimerListAdapter(Context context, int resource, ArrayList<TimerVO> timerList) {
        this.context = context;
        this.resource = resource;
        this.timerList = timerList;
    }

    public ArrayList<TimerVO> getTimerList() {
        return timerList;
    }

    public void setTimerList(ArrayList<TimerVO> timerList) {
        this.timerList = timerList;
    }

    @Override
    public int getCount() {
        return timerList.size();
    }

    @Override
    public Object getItem(int i) {
        return timerList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(resource, viewGroup, false);
        }

        TextView typeText = (TextView) view.findViewById(R.id.typeText);
        TextView periodText = (TextView) view.findViewById(R.id.periodText);
        TextView startText = (TextView) view.findViewById(R.id.startText);

        TimerVO timer = (TimerVO) getItem(i);
        typeText.setText(timer.getType());
        if (timer.getType().equals(TimerVO.TYPE_PAIN)) {
            typeText.setTextColor(Color.RED);
        }
        else if (timer.getType().equals(TimerVO.TYPE_REST)) {
            typeText.setTextColor(Color.BLUE);
        }
        periodText.setText(timer.getPeriod());
        startText.setText(timer.getStart());

        return view;
    }
}
