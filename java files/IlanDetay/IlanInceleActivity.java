package com.example.yusuf.mobilnakliyeyc.IlanDetay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yusuf.mobilnakliyeyc.AdapterWait;
import com.example.yusuf.mobilnakliyeyc.AdapterYenile;
import com.example.yusuf.mobilnakliyeyc.Anlasma;
import com.example.yusuf.mobilnakliyeyc.Chat.ChatActivity;
import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.Me;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;
import com.example.yusuf.mobilnakliyeyc.WaitDialog;
import com.example.yusuf.mobilnakliyeyc.Yorum;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class IlanInceleActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listViewYorumlar;
    private Activity activity;
    private TextView nereye, aciklama, fiyat, aracBilgisi, tarih, nereden, ilanSahibiText, textViewYorumYap;
    private EditText editTextTeklif;
    private Button buttonTeklifVer;
    private ImageView imageViewChat;
    private String ilanVerenID, ilanID;
    private List<Yorum> yorumList = new ArrayList<>();
    private Me ilanSahibi = null;
    private String kullanici_name;
    private Anlasma anlasma = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilan_incele);
        activity = this;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(0x37, 0x08, 0x9e)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        nereden = (TextView) findViewById(R.id.TextViewNereden);
        nereye = (TextView) findViewById(R.id.TextViewNereye);
        aciklama = (TextView) findViewById(R.id.TextViewAciklama);
        fiyat = (TextView) findViewById(R.id.TextViewFiyat);
        aracBilgisi = (TextView) findViewById(R.id.TextViewArac);
        tarih = (TextView) findViewById(R.id.TextViewTarih);
        imageViewChat = (ImageView) findViewById(R.id.ImageViewChat);
        ilanSahibiText = (TextView) findViewById(R.id.TextViewIlanSahibiName);
        ilanSahibiText.setText("");
        listViewYorumlar = (ListView) findViewById(R.id.listViewYorumlar);
        listViewYorumlar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String item = (String) listViewYorumlar.getAdapter().getItem(position);
                    if (item.equals("1")) {
                        new ButunYorumlar().execute();
                        new IlanSahibiHakkinda().execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        imageViewChat.setOnClickListener(this);
        editTextTeklif = (EditText) findViewById(R.id.EditTextTeklif);
        buttonTeklifVer = (Button) findViewById(R.id.ButtonTeklifVer);
        buttonTeklifVer.setOnClickListener(this);
        editTextTeklif.setEnabled(false);
        buttonTeklifVer.setEnabled(false);
        gelenVerileriAl();
        textViewYorumYap = (TextView) findViewById(R.id.textViewYorumYap);
        textViewYorumYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new YorumYapCustomDiaog(activity, kullanici_name, ilanVerenID, textViewYorumYap).show();
            }
        });
        new ButunYorumlar().execute();
        new IlanSahibiHakkinda().execute();
    }

    public void gelenVerileriAl() {
        Bundle extras = getIntent().getExtras();
        String nrden = extras.get("nereden").toString();
        String nry = extras.get("nereye").toString();
        String acklama = extras.get("aciklama").toString();
        String fyat = extras.get("fiyat").toString();
        String tarh = extras.get("tarih").toString();
        String arac = extras.get("aracBilgi").toString();
        ilanVerenID = extras.get("ilanVerenID").toString();
        ilanID = extras.get("ilanID").toString();
        kullanici_name = extras.getString("kullanici_name");
        nereden.setText(nrden);
        nereye.setText(nry);
        aciklama.setText(acklama);
        fiyat.setText(fyat);
        tarih.setText(tarh);
        aracBilgisi.setText(arac);
        ilanSahibiText.setText(kullanici_name);

        String state = extras.getString("stateAnlasma");
        if (state != null && state.equals("1")) {
            new IlanIcinYorumKontrol().execute();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ImageViewChat) {
            if (kullanici_name == null) {
                Toast.makeText(activity, "Lütfen sayfanın yüklenmesini bekleyiniz...", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra("ilanVerenID", ilanVerenID);
            intent.putExtra("ilanID", ilanID);
            intent.putExtra("ilanVerenName", ilanSahibi.getName());
            startActivity(intent);
        } else if (id == R.id.ButtonTeklifVer) {
            String fyt = editTextTeklif.getText().toString().trim();
            if (fyt.equals("")) {
                Toast.makeText(activity, "Eksik Bilgi Girişi...", Toast.LENGTH_SHORT).show();
                return;
            }
            new IlanIcinTeklifVer(fyt).execute();
        }
    }

    // kişi hakkındaki yorumlar
    private class ButunYorumlar extends AsyncTask<String, String, String> {
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/yorumlar.asp";
        private Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            yorumList.clear();
            listViewYorumlar.setAdapter(new AdapterWait(activity));
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> where = new ArrayList<>();
            where.add(new BasicNameValuePair("ID", ilanVerenID));
            String str = httpPost.httpPost(url, "POST", where, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        Object json = jsonArray.get(i);
                        String str = String.valueOf(json);
                        Yorum yorum = gson.fromJson(str, Yorum.class);
                        yorumList.add(yorum);
                    }
                    if (length == 0) {
                        listViewYorumlar.setAdapter(new AdapterYenile(activity, "Henüz hiç yorum yapılmamış"));
                        return;
                    }
                    listViewYorumlar.setAdapter(new AdapterYorumlar(activity, yorumList));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            listViewYorumlar.setAdapter(new AdapterYenile(activity));
            Toast.makeText(activity, "İnternete bağlı olduğunuzdan emin olarak tekrar deneyiniz..", Toast.LENGTH_SHORT).show();
        }
    }

    private class IlanSahibiHakkinda extends AsyncTask<String, String, String> {
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/kullaniciara.asp";
        private Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            yorumList.clear();
            listViewYorumlar.setAdapter(new AdapterWait(activity));
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            if (SharedObjects.me == null) return "";
            List<NameValuePair> where = new ArrayList<>();
            where.add(new BasicNameValuePair("ID", ilanVerenID));
            where.add(new BasicNameValuePair("ilanID", ilanID));
            where.add(new BasicNameValuePair("myID", SharedObjects.me.ID));
            String str = httpPost.httpPost(url, "POST", where, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    int length = jsonArray.length();
                    if (length == 2) {
                        Object json = jsonArray.get(0);
                        String str = String.valueOf(json);
                        ilanSahibi = gson.fromJson(str, Me.class);
                        json = jsonArray.get(1);
                        str = String.valueOf(json);
                        anlasma = gson.fromJson(str, Anlasma.class);
                        if (anlasma.getState().equals("1")) {
                            editTextTeklif.setText(anlasma.getFiyat());
                            buttonTeklifVer.setText("Teklif kabul edildi");
                            editTextTeklif.setTextColor(Color.rgb(00, 153, 38));
                            //bg yeşil
                        } else {
                            editTextTeklif.setText(anlasma.getFiyat());
                            buttonTeklifVer.setText("Teklif beklemede");
                            editTextTeklif.setTextColor(Color.rgb(0xff, 0xaa, 0));
                            //bg sari
                        }

                        return;
                    } else if (length == 1) {
                        Object json = jsonArray.get(0);
                        String str = String.valueOf(json);
                        ilanSahibi = gson.fromJson(str, Me.class);
                        editTextTeklif.setEnabled(true);
                        buttonTeklifVer.setEnabled(true);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(activity, "İnternete bağlı olduğunuzdan emin olarak tekrar deneyiniz..", Toast.LENGTH_SHORT).show();
        }
    }

    private class IlanIcinTeklifVer extends AsyncTask<String, String, String> {
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/teklifver.asp";
        private WaitDialog waitDialog = new WaitDialog(activity, "Teklif iletiliyor..");
        private String fiyat;

        public IlanIcinTeklifVer(String fiyat) {
            this.fiyat = fiyat;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waitDialog.showDialog();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> where = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String tarih = "";
            try {
                tarih = simpleDateFormat.format(new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
            where.add(new BasicNameValuePair("tarih", tarih));
            where.add(new BasicNameValuePair("myID", SharedObjects.me.getID()));
            where.add(new BasicNameValuePair("ilanID", ilanID));
            where.add(new BasicNameValuePair("fiyat", fiyat));
            String str = httpPost.httpPost(url, "POST", where, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            waitDialog.hideDialog();
            if (s != null && s.equals("onay")) {
                Toast.makeText(activity, "Teklif Gönderildi", Toast.LENGTH_SHORT).show();
                editTextTeklif.setEnabled(false);
                buttonTeklifVer.setEnabled(false);
                buttonTeklifVer.setText("Teklif beklemede");
                editTextTeklif.setTextColor(Color.rgb(0xff, 0xaa, 0));
                return;
            }
            Toast.makeText(activity, "İnternete bağlı olduğunuzdan emin olarak tekrar deneyiniz..", Toast.LENGTH_SHORT).show();
        }
    }

    private class IlanIcinYorumKontrol extends AsyncTask<String, String, String> {
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/yorumKontrol.asp";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> where = new ArrayList<>();

            where.add(new BasicNameValuePair("myID", SharedObjects.me.getID()));
            where.add(new BasicNameValuePair("ID", ilanVerenID));
            String str = httpPost.httpPost(url, "POST", where, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                if (s.equals("yapilmadi")) {
                    textViewYorumYap.setVisibility(View.VISIBLE);
                }
                return;
            }
            Toast.makeText(activity, "İnternete bağlı olduğunuzdan emin olarak tekrar deneyiniz..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
