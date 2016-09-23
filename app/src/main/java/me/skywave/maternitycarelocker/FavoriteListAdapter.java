package me.skywave.maternitycarelocker;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class FavoriteListAdapter extends BaseAdapter {
    private Context context;
    private int resource;
    private int checkLimit;
    private ArrayList<String> pkgName;
    private PackageManager pm;
    private boolean[] checkList;

    public FavoriteListAdapter(Context context, int resource, ArrayList<String> pkgName) {
        this.context = context;
        this.resource = resource;
        this.pkgName = pkgName;
        pm = context.getPackageManager();
        checkList = new boolean[pkgName.size()];
        Arrays.fill(checkList, false);
    }

    @Override
    public int getCount() {
        return pkgName.size();
    }

    @Override
    public Object getItem(int position) {
        return pkgName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }

        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(pkgName.get(position));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.nameView);
        ImageView iconView = (ImageView) convertView.findViewById(R.id.iconView);
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        final int checkPos = position;

        try {
            nameView.setText(pm.getApplicationLabel(pm.getApplicationInfo(pkgName.get(position), 0)));
            iconView.setImageDrawable(icon);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        if (getCheckedNum() == 2) {
                            checkBox.setChecked(false);
                            return;
                        }
                        checkList[checkPos] = true;
                    }
                    else {
                        checkList[checkPos] = false;
                    }

                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!checkList[position]) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
        return convertView;
    }

    private int getCheckedNum() {
        int num = 0;
        for (int i = 0; i < pkgName.size(); i++) {
            if (checkList[i]) {
                num++;
            }
        }
        return num;
    }
}
