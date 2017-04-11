package com.example.yusuf.mobilnakliyeyc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by yusuf on 1.03.2017.
 */

public class AdapterYenile extends BaseAdapter {
    private LayoutInflater mInflater;
    private String mesaj = null;

    public AdapterYenile(Activity activity) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public AdapterYenile(Activity activity, String mesaj) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return "1";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        view = mInflater.inflate(R.layout.fragment_yenile, null);
        if (mesaj != null) {
            try {
                TextView t = (TextView) view.findViewById(R.id.TextViewYenile);
                t.setText(mesaj);
            } catch (Exception e) {

            }
        }
        return view;
    }
}