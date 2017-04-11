package com.example.yusuf.mobilnakliyeyc.Login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.Me;
import com.example.yusuf.mobilnakliyeyc.Profile.ProfileTabActivity;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.SqlLiteDB.SqlLiteDB;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;
import com.example.yusuf.mobilnakliyeyc.UyeOl.UyeOlActivity;
import com.example.yusuf.mobilnakliyeyc.WaitDialog;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText eposta, password;
    private Activity activity;
    private Button loginB;
    private TextView textViewKaydol;
    private SqlLiteDB sqlLiteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        sqlLiteDB = new SqlLiteDB(activity);
        girisKontrolEt();
        getSupportActionBar().hide();
        eposta = (EditText) findViewById(R.id.editTextEposta);// ekrandaki komponentlerle eşleştirme yapıyoruz
        password = (EditText) findViewById(R.id.editTextSifre);
        loginB = (Button) findViewById(R.id.buttonLogin);
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//butona tıklandığında
                String psw = password.getText().toString().replace("'", "\'");//şifreyi al
                String mail = eposta.getText().toString().replace("'", "\'");//epostayı al
                if (mail.equals("") || psw.equals("")) { //eposta yada mail boş ise uyarı ver
                    Toast.makeText(activity, "Eksik Bilgi girdiniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Login(mail, psw).execute(); //login işlemini başlat

            }
        });
        textViewKaydol = (TextView) findViewById(R.id.textViewKaydol);
        textViewKaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, UyeOlActivity.class));
                finish();
            }
        });
    }

    public void girisKontrolEt() {
        boolean durum = sqlLiteDB.girisYaptiMi();
        if (durum) {
            startActivity(new Intent(activity, ProfileTabActivity.class));
            activity.finish();
        }
    }

    // extends Asyntask yaparak yeni bir thread oluşturup ana thread'e paralel bir işlem yaptırıyoruz
    private class Login extends AsyncTask<String, String, String> {
        private String email, psw;
        private WaitDialog waitDialog = new WaitDialog(activity, "Giriş Yapılıyor.....");
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://ysfcnky7-001-site1.dtempurl.com/giris.asp";

        public Login(String email, String psw) {
            this.email = email;
            this.psw = psw;

        }

        @Override
        protected void onPreExecute() { //ilk çalışır
            super.onPreExecute();
            waitDialog.showDialog();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> where = new ArrayList<>();//sorgu oluşturuyoruz
            where.add(new BasicNameValuePair("email", email)); ///sunucuya giden parametreler
            where.add(new BasicNameValuePair("password", psw));
            String str = httpPost.httpPost(url, "POST", where, 20000); //sunucya post ediyoruz
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            waitDialog.hideDialog();
            if (!s.equals("") && !s.equals("-1")) {
                Gson gson = new Gson();
                SharedObjects.me = gson.fromJson(s, Me.class);//json'u ME sinifina parse ediyoruz
                startActivity(new Intent(activity, ProfileTabActivity.class)); // anasayfaya git(profil) başlat
                sqlLiteDB.girisYap(SharedObjects.me); // giriş bilgilerini sqlite içine sakla
                activity.finish();// açık olan sayfayı kapat
                return;

            } else if (s.equals("-1")) {
                Toast.makeText(activity, "Lütfen bilgilerinizi kontrol ederek tekrar deneyiniz", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, "internet baglantinizi kontrol ediniz...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
