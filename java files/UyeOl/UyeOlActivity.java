package com.example.yusuf.mobilnakliyeyc.UyeOl;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yusuf.mobilnakliyeyc.HttpGetPost;
import com.example.yusuf.mobilnakliyeyc.Login.MainActivity;
import com.example.yusuf.mobilnakliyeyc.Me;
import com.example.yusuf.mobilnakliyeyc.Profile.ProfileTabActivity;
import com.example.yusuf.mobilnakliyeyc.R;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;
import com.example.yusuf.mobilnakliyeyc.WaitDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yusuf on 1.03.2017.
 */

public class UyeOlActivity extends AppCompatActivity {

    private EditText userName, email, password, phone;
    private Button register;
    private RadioButton radioButtonM, radioButtonN;
    private Activity activity;
    private TextView textViewGiris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaydol);

        getSupportActionBar().hide();
        activity = this;
        userName = (EditText) findViewById(R.id.reg_fullname);
        email = (EditText) findViewById(R.id.reg_email);
        password = (EditText) findViewById(R.id.reg_password);
        phone = (EditText) findViewById(R.id.reg_phone);
        radioButtonM = (RadioButton) findViewById(R.id.radioButton);
        radioButtonN = (RadioButton) findViewById(R.id.radioButton2);
        register = (Button) findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString().replace("'", "\'");
                String mail = email.getText().toString().replace("'", "\'");
                String psw = password.getText().toString().replace("'", "\'");
                String phn = phone.getText().toString().replace("'", "\'");
                if (name.equals("") || mail.equals("") || psw.equals("") || phn.equals("")) {
                    Toast.makeText(activity, "Eksik Bilgi girdiniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = "musteri";
                if (radioButtonN.isChecked()) {
                    state = "nakliyeci";
                }
                new Reqister(mail, psw, state, name, phn).execute();
            }
        });
        textViewGiris = (TextView) findViewById(R.id.textViewGiris);
        textViewGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, MainActivity.class));
                finish();
            }
        });
    }


    private class Reqister extends AsyncTask<String, String, String> {
        private String email, psw, state, adSoyad, phn, ID = UUID.randomUUID().toString();
        private WaitDialog waitDialog = new WaitDialog(activity, "Kayıt işlemi devam ediyor....");
        private HttpGetPost httpPost = new HttpGetPost();
        private String url = "http://erkansuat-001-site1.itempurl.com/kayit.asp";

        public Reqister(String email, String psw, String state, String adSoyad, String phn) {
            this.email = email;
            this.psw = psw;
            this.state = state;
            this.adSoyad = adSoyad;
            this.phn = phn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waitDialog.showDialog();
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("ID", ID));
            values.add(new BasicNameValuePair("email", email));
            values.add(new BasicNameValuePair("state", state));
            values.add(new BasicNameValuePair("name", adSoyad));
            values.add(new BasicNameValuePair("phone", phn));
            values.add(new BasicNameValuePair("password", psw));
            String str = httpPost.httpPost(url, "POST", values, 20000);
            return str.replace("\n", " ").trim();
        }

        @Override
        protected void onPostExecute(String s) {
            waitDialog.hideDialog();
            if (s.equals("onay")) {
                SharedObjects.me=new Me();
                SharedObjects.me.setEmail(email);
                SharedObjects.me.setState(state);
                SharedObjects.me.setID(ID);
                SharedObjects.me.setName(adSoyad);
                SharedObjects.me.setTel(phn);
                SharedObjects.me.setSifre(psw);
                startActivity(new Intent(activity, ProfileTabActivity.class));
                activity.finish();
                return;
            }
            Toast.makeText(activity, "Lütfen bilgilerinizi kontrol ediniz", Toast.LENGTH_SHORT).show();
        }
    }
}