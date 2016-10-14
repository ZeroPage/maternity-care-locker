package me.skywave.maternitycarelocker.locker.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import me.skywave.maternitycarelocker.R;
import me.skywave.maternitycarelocker.locker.core.LockerDialog;

public class InfoListAdapter extends ArrayAdapter<HashMap<String, String>> {

    private ArrayList<HashMap<String, String>> map;

    public InfoListAdapter(Context context, int resource, ArrayList<HashMap<String, String>> map) {
        super(context, resource);
        this.map = map;
    }

    @Override
    public int getCount() {
        return map.size();
    }

    @Override
    public HashMap<String, String> getItem(int i) {
        return map.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.info_list_item, null);
        }

        TextView title = (TextView) view.findViewById(R.id.titleText);
        TextView content = (TextView) view.findViewById(R.id.contentText);
        ImageView icon = (ImageView) view.findViewById(R.id.phoneIcon);

        title.setText(getItem(i).get("item1"));
        content.setText(getItem(i).get("item2"));
        if (getItem(i).get("item2").contains("tel:")) {
            icon.setVisibility(View.VISIBLE);
            final String phoneNumber = getItem(i).get("item2");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    getContext().startActivity(intent);
                    LockerDialog.requestWidget();
                }
            });
        } else {
            icon.setVisibility(View.INVISIBLE);
            view.setOnClickListener(null);
        }
        return view;
    }
}
