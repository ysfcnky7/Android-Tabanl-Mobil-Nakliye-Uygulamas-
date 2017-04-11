package com.example.yusuf.mobilnakliyeyc.Mesajlarim;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.yusuf.mobilnakliyeyc.Chat.ChatActivity;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.SqlLiteDB.SqlLiteDB;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class MesajlarimFragment extends Fragment {
    private Activity activity;
    private SqlLiteDB sqlLiteDB;
    private List<GelenKutusu> gelenKutusuList = new ArrayList<>();
    private ListView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mesajlarim, container, false);
        activity = getActivity();
        sqlLiteDB = new SqlLiteDB(activity);
        gelenKutusuList = sqlLiteDB.gelenkutusu(SharedObjects.me.getID());
        listView = (ListView) view.findViewById(R.id.listViewMesajlarim);
        try {
            listView.setAdapter(new AdapterGelenKutusu(activity, gelenKutusuList));
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    GelenKutusu gelenKutusu = (GelenKutusu) listView.getAdapter().getItem(position);
                    if (gelenKutusu.getUserID() == null) return;
                    Intent intent = new Intent(activity, ChatActivity.class);
                    intent.putExtra("ilanVerenID", gelenKutusu.getUserID());
                    intent.putExtra("ilanID", "");
                    intent.putExtra("ilanVerenName", gelenKutusu.getUserName());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //new Refresh().execute();
        return view;
    }


    private class Refresh extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {
            try {
                while (true) {
                    Thread.sleep(5000);
                    publishProgress();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            gelenKutusuList = sqlLiteDB.gelenkutusu(SharedObjects.me.getID());
            listView.setAdapter(new AdapterGelenKutusu(activity, gelenKutusuList));
        }
    }
}
