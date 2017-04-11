package com.example.yusuf.mobilnakliyeyc;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class HttpGetPost {
    static InputStream veri;
    static String veri_string = "";


    public HttpGetPost() {
        // TODO Auto-generated constructor stub
    }

    public String httpPost(String url, String method, List<NameValuePair> params, int time) {


        try {

            if (method == "POST") {

                HttpParams httpParameters = new BasicHttpParams();
                int timeout1 = time;
                int timeout2 = time;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeout1);
                HttpConnectionParams.setSoTimeout(httpParameters, timeout2);
                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                veri = httpEntity.getContent();

            } else if (method == "GET") {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                veri = httpEntity.getContent();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(veri, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            veri.close();
            veri_string = sb.toString();
        } catch (Exception e) {
            return "";
        }

        return veri_string;

    }

}