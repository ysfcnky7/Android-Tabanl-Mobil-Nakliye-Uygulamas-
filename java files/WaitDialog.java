package com.example.yusuf.mobilnakliyeyc;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by yusuf on 1.03.2017.
 */

public class WaitDialog {

    private Activity activity;
    private String icerik;
    private ProgressDialog ringProgressDialog;

    public WaitDialog(Activity activity, String icerik) {
        this.activity = activity;
        this.icerik = icerik;
    }

    public void showDialog() {
        ringProgressDialog = ProgressDialog.show(activity, "İşlem Gerçekleşiyor Bekleyiniz ...", icerik, true);
        ringProgressDialog.setCancelable(false);
    }
    public void hideDialog() {
        ringProgressDialog.dismiss();
    }
}

