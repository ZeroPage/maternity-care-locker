package me.skywave.maternitycarelocker.companion.preference;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.skywave.maternitycarelocker.R;

public class FavoriteListAdapter extends BaseAdapter {
    private Context context;
    private int resource;
    private ArrayList<String> allPkg;
    private PackageManager pm;
    private Set<String> selectedPkg = new HashSet<>();

    public FavoriteListAdapter(Context context, int resource) {
        this.context = context;
        this.resource = resource;
        pm = context.getPackageManager();

        List<PackageInfo> packages = pm.getInstalledPackages(0);
        allPkg = new ArrayList<>();
        for(PackageInfo pkg : packages) {
            allPkg.add(pkg.packageName);
        }
    }

    @Override
    public int getCount() {
        return allPkg.size();
    }

    @Override
    public Object getItem(int position) {
        return allPkg.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);
        }

        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(allPkg.get(position));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.nameView);
        ImageView iconView = (ImageView) convertView.findViewById(R.id.iconView);
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        try {
            nameView.setText(pm.getApplicationLabel(pm.getApplicationInfo(allPkg.get(position), 0)));
            iconView.setImageDrawable(icon);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        if (selectedPkg.size() == 2) {
                            checkBox.setChecked(false);
                            return;
                        }
                        selectedPkg.add(allPkg.get(position));
                    }
                    else {
                        selectedPkg.remove(allPkg.get(position));
                    }

                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!selectedPkg.contains(allPkg.get(position))) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
        return convertView;
    }

    public Set<String> getCheckedSet() {
        return selectedPkg;
    }
}
