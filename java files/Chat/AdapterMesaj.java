package com.example.yusuf.mobilnakliyeyc.Chat;

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

public class AdapterMesaj extends BaseAdapter {
    private Activity activity;
    private List<Mesaj> mesajList;
    private LayoutInflater mInflater;

    public AdapterMesaj(Activity activity, List<Mesaj> mesajList) {
        this.activity = activity;
        this.mesajList = mesajList;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setMesajList(List<Mesaj> mesajList) {
        this.mesajList = mesajList;
    }

    @Override
    public int getCount() {
        return mesajList.size();
    }

    @Override
    public Object getItem(int position) {
        return mesajList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Mesaj mesaj = mesajList.get(position);
        if (mesaj.getDurum().equals("1")) {//1 gelen mesaj
            view = mInflater.inflate(R.layout.item_receive_message, null);
        } else {
            view = mInflater.inflate(R.layout.item_sending_message, null);
        }
        TextView textViewMessage = (TextView) view.findViewById(R.id.textViewMesaj);
        TextView textViewTarih = (TextView) view.findViewById(R.id.textViewTarih);
        textViewMessage.setText(mesaj.getMesaj());
        textViewTarih.setText(mesaj.getTarih().split(" ")[1]);
        return view;
    }
}
