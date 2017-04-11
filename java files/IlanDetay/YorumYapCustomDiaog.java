package com.example.yusuf.mobilnakliyeyc.IlanDetay;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;
import com.example.yusuf.mobilnakliyeyc.WaitDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class YorumYapCustomDiaog extends Dialog {
    private Activity activity;
    private EditText editTextYorum;
    private RatingBar ratingBar;
    private Button buttonSend;
    private String hedefID, ilanSahibi;
    private TextView textViewYorumYap;

    public YorumYapCustomDiaog(Activity activity, String ilanSahibi, String hedefID, TextView textViewYorumYap) {
        super(activity);
        this.hedefID = hedefID;
        this.ilanSahibi = ilanSahibi;
        this.textViewYorumYap = textViewYorumYap;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_yorum_yap);
        TextView tv = (TextView) findViewById(R.id.textViewKullanici);
        tv.setText(ilanSahibi + " için yorum yap");
        editTextYorum = (EditText) findViewById(R.id.editTextYorum);
        ratingBar = (RatingBar) findViewById(R.id.ratingBarYorum);
        buttonSend = (Button) findViewById(R.id.buttonGonder);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IlanIcinYorumYap().execute();
            }
        });
    }

    private class IlanIcinYorumYap extends AsyncTask<String, String, String> {
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/yorumYap.asp";
        private WaitDialog waitDialog = new WaitDialog(activity, "Yorum gönderiliyor..");
        private String yildiz, yorum;

        public IlanIcinYorumYap() {
            yildiz = ratingBar.getRating() + "";
            yorum = editTextYorum.getText().toString().trim();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(yildiz.equals("0")){
                Toast.makeText(activity, "Yıldız bilgisi girilmedi...", Toast.LENGTH_SHORT).show();
                return;
            }
            waitDialog.showDialog();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            if(yildiz.equals("0")){
                return "";
            }
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
            where.add(new BasicNameValuePair("hedefID", hedefID));
            where.add(new BasicNameValuePair("yorum", yorum));
            where.add(new BasicNameValuePair("yildiz", yildiz));
            String str = httpPost.httpPost(url, "POST", where, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            if(yildiz.equals("0")){
                return;
            }
            waitDialog.hideDialog();
            if (s != null && s.equals("onay")) {
                Toast.makeText(activity, "Yorum  Gönderildi", Toast.LENGTH_SHORT).show();
                textViewYorumYap.setVisibility(View.INVISIBLE);
                dismiss();
                return;
            }
            Toast.makeText(activity, "İnternete bağlı olduğunuzdan emin olarak tekrar deneyiniz..", Toast.LENGTH_SHORT).show();
        }
    }
}
