package com.example.yusuf.mobilnakliyeyc.Profile.Nakliye;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yusuf.mobilnakliyeyc.AdapterWait;
import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;
import com.example.yusuf.mobilnakliyeyc.Teklif;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class GelenTekliflerFragment extends Fragment {
    private Activity activity;
    private ListView listViewGelenTeklifler;
    private List<Teklif> teklifList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gelen_teklifler, container, false);
        activity = getActivity();
        listViewGelenTeklifler = (ListView) view.findViewById(R.id.listViewGelenTeklifler);
        ImageView imageViewYenile = (ImageView) view.findViewById(R.id.imageViewYenile);
        imageViewYenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GelenTeklifler().execute();
            }
        });
        if (teklifList.size() == 0) {
            new GelenTeklifler().execute();
        } else {
            listViewGelenTeklifler.setAdapter(new AdapterGelenTeklifler(activity, teklifList, listViewGelenTeklifler));
        }
        return view;
    }



    private class GelenTeklifler extends AsyncTask<String, String, String> {
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/teklifler.asp";
        private Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            teklifList.clear();
            listViewGelenTeklifler.setAdapter(new AdapterWait(activity));
        }

        @Override
        protected String doInBackground(String... params) {
            String meID = SharedObjects.me.getID();
            List<NameValuePair> where = new ArrayList<>();
            where.add(new BasicNameValuePair("ID", meID));
            String str = httpPost.httpPost(url, "POST", where, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s != null && !s.equals("")) {
                    try {
                        JSONArray jsonArray = new JSONArray(s);
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            Object json = jsonArray.get(i);
                            String str = String.valueOf(json);
                            Teklif teklif = gson.fromJson(str, Teklif.class);
                            teklifList.add(teklif);
                        }
                        listViewGelenTeklifler.setAdapter(new AdapterGelenTeklifler(activity, teklifList, listViewGelenTeklifler));
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listViewGelenTeklifler.setAdapter(null);
                Toast.makeText(activity, "İnternete bağlı olduğunuzdan emin olarak tekrar deneyiniz..", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(activity, "İnternete bağlı olduğunuzdan emin olarak tekrar deneyiniz..", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
