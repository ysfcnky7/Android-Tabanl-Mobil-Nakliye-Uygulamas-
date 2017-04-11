package com.example.yusuf.mobilnakliyeyc.IlanDetay;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Teklif;

/**
 * Created by yusuf on 1.03.2017.
 */

public class IlanDetayCustomDialog extends Dialog implements View.OnClickListener {
    private Teklif teklif;
    private Activity activity;
    private TextView nereye, aciklama, fiyat, aracBilgisi, tarih, nereden, ilanSahibiText;

    public IlanDetayCustomDialog(Activity activity, Teklif teklif) {
        super(activity);
        this.teklif = teklif;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_ilan_detay);
        nereden = (TextView) findViewById(R.id.TextViewNereden);
        nereye = (TextView) findViewById(R.id.TextViewNereye);
        aciklama = (TextView) findViewById(R.id.TextViewAciklama);
        fiyat = (TextView) findViewById(R.id.TextViewFiyat);
        aracBilgisi = (TextView) findViewById(R.id.TextViewArac);
        tarih = (TextView) findViewById(R.id.TextViewTarih);
        nereden.setText(teklif.getNereden());
        nereye.setText(teklif.getNereye());
        aciklama.setText(teklif.getAciklama());
        fiyat.setText(teklif.getFiyatOrjinal());
        aracBilgisi.setText(teklif.getArac());
        tarih.setText(teklif.getTarihOrjinal());

    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

