package com.example.yusuf.mobilnakliyeyc.Profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.yusuf.mobilnakliyeyc.Chat.ChatActivity;
import com.example.yusuf.mobilnakliyeyc.Chat.Mesaj;
import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.IlanOlustur.IlanOlusturActivity;
import com.example.yusuf.mobilnakliyeyc.Login.MainActivity;
import com.example.yusuf.mobilnakliyeyc.Me;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.SqlLiteDB.SqlLiteDB;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yusuf on 1.03.2017.
 */

public class ProfileTabActivity extends AppCompatActivity {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Activity activity;
    private ProfileTabFragment fragment;
    private Me me;
    private SqlLiteDB sqlLiteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tab);
        activity = this;
        sqlLiteDB = new SqlLiteDB(activity);
        me = SharedObjects.me;
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(0x37, 0x08, 0x9e)));
        getSupportActionBar().setTitle(me.getName() == null ? "" : me.getName());
        fragment = new ProfileTabFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fragmentField, fragment, "f1").commit();
        new ListenThread().start();
    }

    private class ListenMessage extends AsyncTask<String, String, String> {
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/mesajlar.asp";
        private HttpGetPost httpPost = new HttpGetPost();
        private Gson gson = new Gson();

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> query = new ArrayList<>();
            if (me == null) return null;
            query.add(new BasicNameValuePair("myID", me.getID()));
            String str = httpPost.httpPost(url, "POST", query, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("") && !s.equals("[]")) {
                try {
                    JSONArray jsonArray = new JSONArray(s);//sunucudan Json Array geliyor
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {// array boyutu kadar dönsün
                        Object o = jsonArray.get(i); // json array içinden sırasıyla json'ları alsın
                        String str = String.valueOf(o);// json'u string yap
                        Mesaj mesaj = gson.fromJson(str, Mesaj.class); // json Stringini Mesaj classıyla eşleştir
                        sqlLiteDB.addMessage(mesaj);//gelen mesajı local db ye ekle
                        bildirimGoster(new Random().nextInt(55555), mesaj);//yeni mesaj diye bildirim çıkar
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private boolean listen = true;

    public class ListenThread extends Thread {
        @Override
        public void run() {
            while (listen && me != null && me.getID() != null) {
                try {
                    new ListenMessage().execute();
                    Thread.sleep(4000);
                } catch (Exception e) {

                }
            }
        }

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void bildirimGoster(int id, Mesaj mesaj) {
        String ilanID = mesaj.getIlanID();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("ilanVerenID", mesaj.getTargetID());
        intent.putExtra("ilanID", ilanID);
        intent.putExtra("ilanVerenName", mesaj.getGonderenAdi());
        PendingIntent pIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        Notification mNotification = new Notification.Builder(activity)

                .setContentTitle(mesaj.getGonderenAdi())
                .setContentText(mesaj.getMesaj())
                .setSmallIcon(R.drawable.chat)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .build();

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;//notificationa tıklanınca notificationın otomatik silinmesi için
        mNotification.defaults |= Notification.DEFAULT_SOUND;//notification geldiğinde bildirim sesi çalması için
        mNotification.defaults |= Notification.DEFAULT_VIBRATE;//notification geldiğinde bildirim titremesi için
        notificationManager.notify(id, mNotification);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (SharedObjects.me.getState().equals("nakliyeci"))
            getMenuInflater().inflate(R.menu.menu_main, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main_musteri, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Cikis) {
            listen = false;
            sqlLiteDB.cikisYap();//veritabanından kaydı sil
            SharedObjects.me = null;// kişisel bilgilerimi boşalt
            startActivity(new Intent(activity, MainActivity.class)); // girişyap ekranını çağır
            activity.finish();// bu sayfayı kapat
        } else if (id == R.id.IlanOlustur || id == R.id.IlanOlustur2) {
            if (SharedObjects.me.getState().equals("nakliyeci"))
                startActivity(new Intent(activity, IlanOlusturActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
