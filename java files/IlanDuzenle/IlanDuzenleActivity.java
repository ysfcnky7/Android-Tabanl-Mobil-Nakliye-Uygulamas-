package com.example.yusuf.mobilnakliyeyc.IlanDuzenle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.ILLER;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;
import com.example.yusuf.mobilnakliyeyc.WaitDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class IlanDuzenleActivity extends AppCompatActivity {
    private EditText nereden, nereye, aracBilgisi, fiyat, aciklama, tarih;
    private Button buttonOlustur;
    private Activity activity;
    private String ilanID, state;
    private RadioButton radioButtonAktif, radioButtonPasif;
    private Spinner spinnerNereden, spinnerNereye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nakliye_ilan_duzenle);
        activity = this;
        //#4ccbff
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(0x37, 0x08, 0x9e)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        radioButtonAktif = (RadioButton) findViewById(R.id.radioButtonAktif);
        radioButtonPasif = (RadioButton) findViewById(R.id.radioButtonPasif);
        // nereden = (EditText) findViewById(R.id.editTextNereden);
        // nereye = (EditText) findViewById(R.id.editTextNereye);
        aracBilgisi = (EditText) findViewById(R.id.editTextAracBilgisi);
        fiyat = (EditText) findViewById(R.id.editTextFiyat);
        aciklama = (EditText) findViewById(R.id.editTextAcikalam);
        tarih = (EditText) findViewById(R.id.editTextTarih);
        buttonOlustur = (Button) findViewById(R.id.buttonOlustur);
        buttonOlustur.setText("Güncelle");
        buttonOlustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String from = nereden.getText().toString().trim();
                // String to = nereye.getText().toString().trim();
                String from = spinnerNereden.getSelectedItem().toString().trim();
                String to = spinnerNereye.getSelectedItem().toString().trim();
                String aracbilgi = aracBilgisi.getText().toString().trim();
                String fiyt = fiyat.getText().toString().trim();
                String acklma = aciklama.getText().toString().trim();
                String trh = tarih.getText().toString().trim();
                if (from.equals("") || to.equals("") || aracbilgi.equals("") || fiyt.equals("") || trh.equals("")) {
                    Toast.makeText(activity, "Eksi Bilgi Girişi", Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = "1";
                if (radioButtonPasif.isChecked()) state = "0";
                new IlanDuzenle(ilanID, to, from, aracbilgi, fiyt, acklma, trh, state).execute();
            }
        });
        spinnerNereden = (Spinner) findViewById(R.id.spinnerNereden);
        spinnerNereden.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, ILLER.ILLER));
        spinnerNereye = (Spinner) findViewById(R.id.spinnerNereye);
        spinnerNereye.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, ILLER.ILLER));
        gelenVerileriAl();
    }

    public void gelenVerileriAl() {
        Bundle extras = getIntent().getExtras();
        String nrden = extras.get("nereden").toString();
        String nry = extras.get("nereye").toString();
        String acklama = extras.get("aciklama").toString();
        String fyat = extras.get("fiyat").toString();
        String tarh = extras.get("tarih").toString();
        String arac = extras.get("aracBilgi").toString();
        ilanID = extras.get("ID").toString();
        state = extras.get("state").toString();
        if (state.equals("1")) {
            radioButtonAktif.setChecked(true);
            radioButtonPasif.setChecked(false);
        } else {
            radioButtonAktif.setChecked(false);
            radioButtonPasif.setChecked(true);
        }
        int indisNerden = 0;
        for (int i = 0; i < ILLER.ILLER.length; i++) {
            if (ILLER.ILLER[i].toLowerCase().equals(nrden.toLowerCase())) {
                indisNerden = i;
                break;
            }
        }
        spinnerNereden.setSelection(indisNerden);
        int indisNereye = 0;
        for (int i = 0; i < ILLER.ILLER.length; i++) {
            if (ILLER.ILLER[i].toLowerCase().equals(nry.toLowerCase())) {
                indisNereye = i;
                break;
            }
        }
        spinnerNereye.setSelection(indisNereye);
        aciklama.setText(acklama);
        fiyat.setText(fyat);
        tarih.setText(tarh);
        aracBilgisi.setText(arac);
    }

    private class IlanDuzenle extends AsyncTask<String, String, String> {
        private WaitDialog waitDialog = new WaitDialog(activity, "İlan Güncelleniyor.....");
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/ilanduzenle.asp";
        private String to, from, aracbilgi, fyt, acklama, trh, ilanID, state;

        public IlanDuzenle(String ilanID, String to, String from, String aracbilgi, String fit, String acklama, String trh, String state) {
            this.to = to;
            this.from = from;
            this.aracbilgi = aracbilgi;
            this.fyt = fit;
            this.acklama = acklama;
            this.trh = trh;
            this.ilanID = ilanID;
            this.state = state;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waitDialog.showDialog();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {

            String meID = SharedObjects.me.getID();
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("nereden", from));
            values.add(new BasicNameValuePair("nereye", to));
            values.add(new BasicNameValuePair("arac", aracbilgi));
            values.add(new BasicNameValuePair("aciklama", acklama));
            values.add(new BasicNameValuePair("kullanici_ID", meID));
            values.add(new BasicNameValuePair("tarih", trh));
            values.add(new BasicNameValuePair("fiyat", fyt));
            values.add(new BasicNameValuePair("state", state));
            values.add(new BasicNameValuePair("ID", ilanID));
            String str = httpPost.httpPost(url, "POST", values, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            waitDialog.hideDialog();
            if (s.equals("onay")) {
                Toast.makeText(activity, "İlan Başarıyla Güncellendi", Toast.LENGTH_SHORT).show();
                activity.finish();
                return;
            }
            Toast.makeText(activity, "İnternetten kaynaklanan bir sorun oluştu, Lütfen tekrar deneyiniz", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
