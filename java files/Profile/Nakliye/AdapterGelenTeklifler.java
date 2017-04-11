package com.example.yusuf.mobilnakliyeyc.Profile.Nakliye;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.IlanDetay.IlanDetayCustomDialog;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Teklif;
import com.example.yusuf.mobilnakliyeyc.WaitDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class AdapterGelenTeklifler extends BaseAdapter {
    private Activity activity;
    private List<Teklif> teklifList;
    private LayoutInflater mInflater;
    private ListView listView;

    public AdapterGelenTeklifler(Activity activity, List<Teklif> teklifList, ListView listView) {
        this.activity = activity;
        this.teklifList = teklifList;
        this.listView = listView;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return teklifList.size();
    }

    @Override
    public Object getItem(int position) {
        return teklifList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final Teklif teklif = teklifList.get(position);
        view = mInflater.inflate(R.layout.item_teklifler, null);
        TextView nereden = (TextView) view.findViewById(R.id.TextViewNereden);
        TextView nereye = (TextView) view.findViewById(R.id.TextViewNereye);
        EditText fiyat = (EditText) view.findViewById(R.id.editTextfiyat);
        EditText adSoyad = (EditText) view.findViewById(R.id.editTextfullname);
        nereden.setText(teklif.getNereden());
        nereye.setText(teklif.getNereye());
        fiyat.setText(teklif.getFiyat());
        adSoyad.setText(teklif.getName());
        TextView textViewIncele = (TextView) view.findViewById(R.id.textViewNext);
        Button buttonRed = (Button) view.findViewById(R.id.buttonReddet);
        Button buttonKabul = (Button) view.findViewById(R.id.buttonKabulEt);
        buttonKabul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OnayYadaRed("1", teklif).execute();
            }
        });
        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OnayYadaRed("-1", teklif).execute();
            }
        });
        textViewIncele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IlanDetayCustomDialog(activity, teklif).show();
            }
        });
        return view;
    }

    private class OnayYadaRed extends AsyncTask<String, String, String> {
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/redyadakabul.asp";
        private String state;
        private Teklif teklif;
        private WaitDialog waitDialog = new WaitDialog(activity, "Cevap gönderiliyor...");


        public OnayYadaRed(String state, Teklif teklif) {
            this.state = state;
            this.teklif = teklif;
        }

        @Override
        protected void onPreExecute() {
            waitDialog.showDialog();
            super.onPreExecute();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> where = new ArrayList<>();
            where.add(new BasicNameValuePair("ID", teklif.getTeklifVerenID()));
            where.add(new BasicNameValuePair("ilanID", teklif.getIlan_ID()));
            where.add(new BasicNameValuePair("state", state));
            String str = httpPost.httpPost(url, "POST", where, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            waitDialog.hideDialog();
            if (s != null && s.equals("onay")) {
                teklifList.remove(teklif);
                AdapterGelenTeklifler adapter = (AdapterGelenTeklifler) listView.getAdapter();
                adapter.notifyDataSetChanged();
                if (state.equals("1")) {
                    Toast.makeText(activity, "Teklifi kabul ettiniz lütfen ilan için gerekli düzenlemeyi yapınız...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, "Teklifi reddedildi...", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            Toast.makeText(activity, "İnternete bağlı olduğunuzdan emin olarak tekrar deneyiniz..", Toast.LENGTH_SHORT).show();
        }
    }

}
