package com.example.yusuf.mobilnakliyeyc.IlanDetay;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Yorum;

import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class AdapterYorumlar extends BaseAdapter {
    private LayoutInflater mInflater;
    private Activity activity;
    private List<Yorum> yorumList;

    public AdapterYorumlar(Activity activity, List<Yorum> yorumList) {
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.yorumList = yorumList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return yorumList.size();
    }

    @Override
    public Object getItem(int position) {
        return yorumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        view = mInflater.inflate(R.layout.item_yorum, null);
        TextView tvadSoyad = (TextView) view.findViewById(R.id.textViewAdSoyad);
        EditText editTextYorum = (EditText) view.findViewById(R.id.editTextYorum);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBarYorum);
        Yorum yorum = yorumList.get(position);
        tvadSoyad.setText("Yorum Yapan: " + yorum.getName());
        editTextYorum.setText(yorum.getYorum());
        ratingBar.setRating(Float.parseFloat(yorum.getYildiz()));
        return view;
    }
}
