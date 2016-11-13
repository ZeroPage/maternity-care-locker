package me.skywave.maternitycarelocker.companion;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.model.BabyfairVO;

/**
 * Created by Jeong on 2016. 11. 13..
 */

public class EventListAdapter extends ArrayAdapter<BabyfairVO> {
    private List<BabyfairVO> fairVOs;
    private Context context;
    private int resource;

    public EventListAdapter(Context context, int resource, List<BabyfairVO> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.fairVOs = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if(row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            row = layoutInflater.inflate(resource, parent, false);
        }

        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);

        TextView title = (TextView) row.findViewById(R.id.tv_title);
        TextView date = (TextView) row.findViewById(R.id.tv_date);
        TextView desc = (TextView) row.findViewById(R.id.tv_content);

        title.setText(fairVOs.get(position).getTitle());
        date.setText(format.format(fairVOs.get(position).getDateFrom()) + " ~ " + format.format(fairVOs.get(position).getDateTo()));
        desc.setText(fairVOs.get(position).getDescription());

        return row;
    }
}
