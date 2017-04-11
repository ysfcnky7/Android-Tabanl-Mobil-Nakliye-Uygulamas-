package com.example.yusuf.mobilnakliyeyc.SqlLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yusuf.mobilnakliyeyc.Chat.Mesaj;
import com.example.yusuf.mobilnakliyeyc.Me;
import com.example.yusuf.mobilnakliyeyc.Mesajlarim.GelenKutusu;
import com.example.yusuf.mobilnakliyeyc.Tampon.SharedObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuf on 1.03.2017.
 */

public class SqlLiteDB extends SQLiteOpenHelper {
    private static String DBNAME = "mydb";
    private static int VERSION = 1;

    private static final String GIRIS_TABLO = "GirisTablo";
    private static final String ID = "myid";
    private static final String NAME = "name";
    private static final String TELEFON = "tel";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "sifre";
    private static final String STATE = "state";


    private static final String TABLE_MESAJLAR = "mesajlar";
    private static final String MYID = "myid";// kendi ID'm
    private static final String HEDEFID = "hedefid";//alıcı id si
    private static final String HEDEFNAME = "hedefname";//hedef kişinin adıSoyadı
    private static final String MESAJ = "mesaj";//mesaj içeriği
    private static final String DATE = "date";//mesaj tarihi
    private static final String DURUM = "durum";//gelen-giden;
    //  private static final String STATE = "state";//mesaj db'den okundumu 0=hayır,1=evet

    public SqlLiteDB(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_TABLE = "CREATE TABLE " + GIRIS_TABLO + "("
                    + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ID + " TEXT,"
                    + NAME + " TEXT,"
                    + TELEFON + " TEXT,"
                    + EMAIL + " TEXT,"
                    + STATE + " TEXT,"
                    + PASSWORD + " TEXT  )";
            db.execSQL(CREATE_TABLE);

            CREATE_TABLE = "CREATE TABLE " + TABLE_MESAJLAR + "("
                    + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + MYID + " TEXT,"
                    + HEDEFID + " TEXT,"
                    + HEDEFNAME + " TEXT,"
                    + MESAJ + " TEXT,"
                    + DATE + " TEXT,"
                    + STATE + " TEXT,"
                    + DURUM + " TEXT  )";
            db.execSQL(CREATE_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //////Mesajlaşma////
    public void addMessage(Mesaj mesaj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MYID, mesaj.getMyID());
        values.put(HEDEFID, mesaj.getTargetID());
        values.put(HEDEFNAME, mesaj.getGonderenAdi());
        values.put(MESAJ, mesaj.getMesaj());
        values.put(DATE, mesaj.getTarih());
        values.put(DURUM, mesaj.getDurum());
        values.put(STATE, mesaj.getState());
        db.insertOrThrow(TABLE_MESAJLAR, null, values);
        db.close();
    }

    public List<Mesaj> getMessages(String targetID, String my_ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteDatabase db2 = this.getWritableDatabase();
        String selectQuery = "";
        if (targetID != null) {
            selectQuery = "SELECT * FROM " + TABLE_MESAJLAR + " WHERE " + HEDEFID + " = '" + targetID + "' and " + MYID + "='" + my_ID + "'";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_MESAJLAR + " WHERE " + MYID + "='" + my_ID + "'";
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Mesaj> messages = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int ID = cursor.getInt(0);
                String myID = cursor.getString(1);
                String hedef_ID = cursor.getString(2);
                String hedefName = cursor.getString(3);
                String mesaj = cursor.getString(4);
                String date = cursor.getString(5);
                String state = cursor.getString(6);
                String durum = cursor.getString(7);
                Mesaj m = new Mesaj(mesaj, date, durum, myID, hedef_ID, hedefName, state);
                if (state != null && state.equals("0")) {
                    ContentValues values = new ContentValues();
                    values.put(STATE, "1");
                    int count = db2.update(TABLE_MESAJLAR, values, "ID" + " = ?", new String[]{ID + ""});
                }
                messages.add(m);
            } while (cursor.moveToNext());
        }
        db.close();
        db2.close();
        return messages;
    }

    public List<GelenKutusu> gelenkutusu(String myID) {//amaç mesajlaşılan kişinin adını ve varsa yeni mesaj sayısını bulmak
        List<GelenKutusu> inBoxList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] sutunlar = {HEDEFNAME, HEDEFID, "count(*)"};

        //where-where-group by-having-order by
        //önce  yeni gelen mesajlar okundu=hayır=0
        Cursor cursor = db.query(TABLE_MESAJLAR, sutunlar, STATE + "=? and " + MYID + "=?", new String[]{"0", myID}, HEDEFID, null, "ID desc");
        if (cursor.moveToFirst()) {
            do {
                String targetName = cursor.getString(0);
                String targetID = cursor.getString(1);
                GelenKutusu m = new GelenKutusu(targetName, targetID, true);
                inBoxList.add(m);
            } while (cursor.moveToNext());
        }
        // sonra eski mesajlar  için: okundu=evet=1
        cursor = db.query(TABLE_MESAJLAR, sutunlar, STATE + "=? and " + MYID + "=?", new String[]{"1", myID}, HEDEFID, null, "ID desc");
        if (cursor.moveToFirst()) {
            do {
                String targetName = cursor.getString(0);
                String targetID = cursor.getString(1);
                GelenKutusu m = new GelenKutusu(targetName, targetID, false);
                boolean ekle = true;
                for (GelenKutusu u : inBoxList) {
                    if (u.getUserID().equals(targetID)) {
                        ekle = false;
                        break;
                    }
                }
                if (ekle) inBoxList.add(m);
            } while (cursor.moveToNext());
        }

        db.close();
        if (inBoxList.size() == 0) {
            GelenKutusu g = new GelenKutusu("Gelen Kutunuz Boş",null,  false);
            inBoxList.add(g);
        }
        return inBoxList;
    }

    ////////////////////

    //////////////////////////////
    //kullanıcı login bilgilerini ekle-çek-sil-giriş yaptımı kontrol et metotları bulunmakta
    /////////////////////////////////
    public void girisYap(Me me) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, me.getID());
        values.put(NAME, me.getName());
        values.put(TELEFON, me.getTel());
        values.put(EMAIL, me.getEmail());
        values.put(STATE, me.getState());
        values.put(PASSWORD, me.getSifre());
        db.insertOrThrow(GIRIS_TABLO, null, values);
        db.close();
    }

    public void cikisYap() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(GIRIS_TABLO, null, null);
        db.close();
    }

    public boolean girisYaptiMi() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select *from " + GIRIS_TABLO;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        boolean giris = false;
        int sayi = cursor.getCount();
        if (sayi == 1) {
            Me me = new Me();
            me.setID(cursor.getString(1));//indisleri Tablo oluşturma sırasına göre seçiyoruz 0.indis=ID, 1.indis=myID , 2.indis=Name .....
            me.setName(cursor.getString(2));
            me.setTel(cursor.getString(3));
            me.setEmail(cursor.getString(4));
            me.setState(cursor.getString(5));
            me.setSifre(cursor.getString(6));
            SharedObjects.me = me;
            giris = true;
        }
        db.close();
        return giris;
    }
    //////////////////////////////////////////////////////////////////////////////////////
}