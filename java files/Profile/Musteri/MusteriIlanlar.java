package com.example.yusuf.mobilnakliyeyc.Profile.Musteri;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yusuf.mobilnakliyeyc.AdapterWait;
import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.Ilan;
import com.example.yusuf.mobilnakliyeyc.IlanDetay.IlanInceleActivity;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class MusteriIlanlar extends Fragment {
    private Activity activity;
    private ListView listViewIlanlarim;
    private List<Ilan> ilanList = new ArrayList<>();
    private ImageView imageViewYenile;
    private EditText editTextNereden, editTextNereye;
    private String searchNereden = "", searchNereye = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nakliye_ilanlar, container, false);
        activity = getActivity();
        editTextNereden = (EditText) view.findViewById(R.id.editTextNereden);
        editTextNereye = (EditText) view.findViewById(R.id.editTextNereye);
        listViewIlanlarim = (ListView) view.findViewById(R.id.listViewIlanlarim);
        imageViewYenile = (ImageView) view.findViewById(R.id.imageViewYenile);
        imageViewYenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ButunIlanlar().execute();
            }
        });
        new ButunIlanlar().execute();
        listViewIlanlarim.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Ilan ilan = (Ilan) listViewIlanlarim.getAdapter().getItem(position);
                    Intent intent = new Intent(activity, IlanInceleActivity.class);
                    intent.putExtra("nereden", ilan.getNereden());
                    intent.putExtra("nereye", ilan.getNereye());
                    intent.putExtra("aciklama", ilan.getAciklama());
                    intent.putExtra("fiyat", ilan.getFiyat());
                    intent.putExtra("tarih", ilan.getTarih());
                    intent.putExtra("aracBilgi", ilan.getArac());
                    intent.putExtra("ilanVerenID", ilan.getKullanici_ID());
                    intent.putExtra("ilanID", ilan.getID());
                    intent.putExtra("kullanici_name", ilan.getKullanici_name());
                    intent.putExtra("stateAnlasma", ilan.getStateAnlasma());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        editTextNereden.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchNereden = s.toString().toLowerCase();
                if (s.length() % 3 == 0) {//her 3 karakterde bir arasın
                    List<Ilan> ilanList2 = new ArrayList<>();
                    for (Ilan ilan : ilanList) {
                        if (ilan.getNereden().toLowerCase().contains(searchNereden)) {
                            if (!searchNereye.equals("")) {
                                if (ilan.getNereye().toLowerCase().contains(searchNereye)) {
                                    ilanList2.add(ilan);
                                }
                            } else {
                                ilanList2.add(ilan);
                            }
                        }
                    }
                    listViewIlanlarim.setAdapter(new AdapterButunIlnlar(activity, ilanList2));
                }
            }
        });
        editTextNereye.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchNereye = s.toString().toLowerCase();
                if (s.length() % 3 == 0) {//her 3 karakterde bir arasın
                    List<Ilan> ilanList2 = new ArrayList<>();
                    for (Ilan ilan : ilanList) {
                        if (ilan.getNereye().toLowerCase().contains(searchNereye)) {
                            if (!searchNereden.equals("")) {
                                if (ilan.getNereden().toLowerCase().contains(searchNereden)) {
                                    ilanList2.add(ilan);
                                }
                            } else {
                                ilanList2.add(ilan);
                            }
                        }
                    }
                    listViewIlanlarim.setAdapter(new AdapterButunIlnlar(activity, ilanList2));
                }
            }
        });
        return view;
    }

    private class ButunIlanlar extends AsyncTask<String, String, String> {
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/ilanlar.asp";
        private Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ilanList.clear();
            listViewIlanlarim.setAdapter(new AdapterWait(activity));
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            if (SharedObjects.me == null) return "";
            List<NameValuePair> where = new ArrayList<>();
            where.add(new BasicNameValuePair("myID", SharedObjects.me.getID()));
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
                        Ilan ilan = gson.fromJson(str, Ilan.class);
                        ilanList.add(ilan);
                    }
                    listViewIlanlarim.setAdapter(new AdapterButunIlnlar(activity, ilanList));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            listViewIlanlarim.setAdapter(null);
            Toast.makeText(activity, "İnternete bağlı olduğunuzdan emin olarak tekrar deneyiniz..", Toast.LENGTH_SHORT).show();
        }
    }
}