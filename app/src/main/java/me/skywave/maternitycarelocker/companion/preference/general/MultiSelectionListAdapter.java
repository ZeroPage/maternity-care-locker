package me.skywave.maternitycarelocker.companion.preference.general;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import me.skywave.maternitycarelocker.R;

public class MultiSelectionListAdapter extends BaseAdapter {
    private Context context;
    private int resource;
    private Set<String> selected = new HashSet<>();
    private Set<Integer> index = new HashSet<>();
    private ArrayList<String> stringList;
    private ArrayList<Drawable> iconList;
    private int limit;

    public MultiSelectionListAdapter(Context context, int resource, ArrayList<String> stringList, ArrayList<Drawable> iconList, int limit) {
        this.context = context;
        this.resource = resource;
        this.stringList = stringList;
        this.iconList = iconList;
        this.limit = limit;
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iconView);
            viewHolder.name = (TextView) convertView.findViewById(R.id.nameView);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.icon.setImageDrawable(iconList.get(position));
        viewHolder.name.setText(stringList.get(position));
        viewHolder.checkBox.setChecked(selected.contains(stringList.get(position)));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.checkBox.isChecked()) {
                    if (selected.size() == limit) {
                        return;
                    }
                    viewHolder.checkBox.setChecked(true);
                    selected.add(stringList.get(position));
                    index.add(position);
                }
                else {
                    viewHolder.checkBox.setChecked(false);
                    selected.remove(stringList.get(position));
                    index.remove(position);
                }

            }
        });

        return convertView;
    }

    public Set<Integer> getCheckedIndex() {
        return index;
    }

    public Set<String> getCheckedSet() {
        return selected;
    }

    public class ViewHolder {
        public ImageView icon;
        public TextView name;
        public CheckBox checkBox;
    }
}
