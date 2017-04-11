package com.example.yusuf.mobilnakliyeyc.Profile.Musteri;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yusuf.mobilnakliyeyc.Ilan;
import com.example.yusuf.mobilnakliyeyc.R;

import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class AdapterButunIlnlar extends BaseAdapter {
    private Activity activity;
    private List<Ilan> ilanList;
    private LayoutInflater mInflater;

    public AdapterButunIlnlar(Activity activity, List<Ilan> ilanList) {
        this.activity = activity;
        this.ilanList = ilanList;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ilanList.size();
    }

    @Override
    public Object getItem(int position) {
        return ilanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Ilan ilan = ilanList.get(position);
        view = mInflater.inflate(R.layout.item_butun_ilanlar, null);
        TextView nereden = (TextView) view.findViewById(R.id.TextViewNereden);
        TextView nereye = (TextView) view.findViewById(R.id.TextViewNereye);
        TextView fiyat = (TextView) view.findViewById(R.id.TextViewFiyat);
        TextView aracBilgisi = (TextView) view.findViewById(R.id.TextViewArac);
        TextView tarih = (TextView) view.findViewById(R.id.TextViewTarih);

        nereden.setText(ilan.getNereden());
        nereye.setText(ilan.getNereye());
        fiyat.setText(ilan.getFiyat());
        aracBilgisi.setText(ilan.getArac());
        tarih.setText(ilan.getTarih());

        TextView incele = (TextView) view.findViewById(R.id.textViewNext);
        if (ilan.getStateAnlasma().equals("1")) {
            incele.setBackgroundResource(R.color.yesil);
            incele.setText("ANLAŞILDI");
        } else if (ilan.getStateAnlasma().equals("0")) {
            incele.setBackgroundResource(R.color.orange);
            incele.setText("TEKLİF BEKLEMEDE");
        } else if (ilan.getStateAnlasma().equals("-1")) {
            incele.setBackgroundResource(R.color.kirmizi);
            incele.setText("TEKLİF REDDEDİLDİ");
        }
        return view;
    }

}
