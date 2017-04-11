package com.example.yusuf.mobilnakliyeyc.Mesajlarim;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yusuf.mobilnakliyeyc.R;

import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class AdapterGelenKutusu extends BaseAdapter {
    private Activity activity;
    private List<GelenKutusu> gelenKutusuList;
    private LayoutInflater mInflater;

    public AdapterGelenKutusu(Activity activity, List<GelenKutusu> gelenKutusuList) {
        this.activity = activity;
        this.gelenKutusuList = gelenKutusuList;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setMessageList(List<GelenKutusu> inBoxList) {
        this.gelenKutusuList = inBoxList;
    }

    @Override
    public int getCount() {

        return gelenKutusuList.size();
    }

    @Override
    public Object getItem(int position) {
        return gelenKutusuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View satirView;
        GelenKutusu gk = gelenKutusuList.get(position);
        satirView = mInflater.inflate(R.layout.item_gelenkutusu, null);
        TextView usernameTextView = (TextView) satirView.findViewById(R.id.textViewUserName);
        usernameTextView.setText(gk.getUserName());
        if (gk.isOkunayanMesaj()) {
            usernameTextView.setTextColor(activity.getResources().getColor(R.color.orange));
        }
        return satirView;
    }
}
