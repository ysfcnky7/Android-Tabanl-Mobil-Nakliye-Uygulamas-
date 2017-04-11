package com.example.yusuf.mobilnakliyeyc.Chat;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.Me;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.SqlLiteDB.SqlLiteDB;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;
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

public class ChatActivity extends AppCompatActivity {
    private Activity activity;
    private String ilanVerenID, ilanID, ilanVerenName;
    private ListView listViewMesajlar;
    private EditText editTextMesaj;
    private Button buttonGonder;
    private Me me;
    private List<Mesaj> mesajList = new ArrayList<>();
    private SqlLiteDB sqlLiteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        activity = this;
        sqlLiteDB = new SqlLiteDB(activity);
        gelenVerileriAl();
        getSupportActionBar().setTitle(ilanVerenName);
        me = SharedObjects.me;
        mesajList = sqlLiteDB.getMessages(ilanVerenID, me.getID());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(0x37, 0x08, 0x9e)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listViewMesajlar = (ListView) findViewById(R.id.listViewMesajlar);
        listViewMesajlar.setAdapter(new AdapterMesaj(activity, mesajList));
        editTextMesaj = (EditText) findViewById(R.id.editTextMessage);
        buttonGonder = (Button) findViewById(R.id.buttonSend);
        buttonGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mesaj = editTextMesaj.getText().toString().trim();
                if (mesaj.equals("")) return;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String tarih = "";
                try {
                    tarih = simpleDateFormat.format(new Date());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Mesaj mesaj1 = new Mesaj(mesaj, tarih, "0", me.getID(), ilanVerenID, ilanVerenName, "1");
                mesajList.add(mesaj1);
                AdapterMesaj adapterMesaj = (AdapterMesaj) listViewMesajlar.getAdapter();
                adapterMesaj.setMesajList(mesajList);
                adapterMesaj.notifyDataSetChanged();
                listViewMesajlar.setSelection(mesajList.size() - 1);
                sqlLiteDB.addMessage(mesaj1);
                editTextMesaj.setText("");
                new MesajGonder(mesaj, tarih).execute();
            }
        });
        new ListenThread().start();
    }

    public void gelenVerileriAl() {//extra ile gönderilen verileri alıyoruz
        Bundle extras = getIntent().getExtras();
        ilanVerenID = extras.get("ilanVerenID").toString();
        ilanID = extras.getString("ilanID");
        ilanVerenName = extras.get("ilanVerenName").toString();
    }


    private class MesajGonder extends AsyncTask<String, String, String> {
        private String msj;
        private String tarih;
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/mesajgonder.asp";
        private HttpGetPost httpPost = new HttpGetPost();

        public MesajGonder(String msj, String tarih) {
            this.msj = msj;
            this.tarih = tarih;
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> query = new ArrayList<>();
            query.add(new BasicNameValuePair("hedefID", ilanVerenID));
            query.add(new BasicNameValuePair("ilanID", ilanID));
            query.add(new BasicNameValuePair("mesaj", msj));
            query.add(new BasicNameValuePair("tarih", tarih));
            query.add(new BasicNameValuePair("gonderenID", me.getID()));
            String str = httpPost.httpPost(url, "POST", query, 20000);
            return str;
        }
    }

    private class ListenMessage extends AsyncTask<String, String, String> {
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/mesajlar.asp";
        private HttpGetPost httpPost = new HttpGetPost();
        private Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            mesajList = sqlLiteDB.getMessages(ilanVerenID, me.getID());
            AdapterMesaj adapterMesaj = (AdapterMesaj) listViewMesajlar.getAdapter();
            adapterMesaj.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> query = new ArrayList<>();
            query.add(new BasicNameValuePair("myID", me.getID()));
            query.add(new BasicNameValuePair("hedefID", ilanVerenID));
            if (ilanVerenID == null)
                return "";
            String str = httpPost.httpPost(url, "POST", query, 20000);
            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("") && !s.equals("[]")) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        Object o = jsonArray.get(i);
                        String str = String.valueOf(o);
                        Mesaj mesaj = gson.fromJson(str, Mesaj.class);
                        mesajList.add(mesaj);
                        sqlLiteDB.addMessage(mesaj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AdapterMesaj adapterMesaj = (AdapterMesaj) listViewMesajlar.getAdapter();
                adapterMesaj.setMesajList(mesajList);
                adapterMesaj.notifyDataSetChanged();
                listViewMesajlar.setSelection(adapterMesaj.getCount() - 1);
            }
        }
    }

    private boolean listen = true;

    public class ListenThread extends Thread {
        @Override
        public void run() {
            while (listen && ilanVerenID != null) {
                try {
                    new ListenMessage().execute();
                    Thread.sleep(4000);
                } catch (Exception e) {

                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        listen = false;
        ilanVerenID = null;
        finish();
        super.onBackPressed();
    }
}
