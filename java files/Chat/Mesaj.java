package com.example.yusuf.mobilnakliyeyc.Chat;

/**
 * Created by yusuf on 1.03.2017.
 */

public class Mesaj {
    private String mesaj;
    private String tarih;
    private String durum;//1=gelen mesaj, 0=giden mesaj
    private String myID;
    private String targetID;//hedef kişinin ID'si
    private String gonderenAdi;//karşıdaki kişinin adı soyadı
    private String state;//local db den veri çekerken kullanıyoruz, yeni mesajları ayırt edebilmek için
    private String ilanID;//hangi ilan için konuşuldu;

    public Mesaj(String mesaj, String tarih, String durum, String myID, String targetID, String gonderenAdi, String state) {
        this.mesaj = mesaj;
        this.tarih = tarih;
        this.durum = durum;
        this.myID = myID;
        this.targetID = targetID;
        this.gonderenAdi = gonderenAdi;
        this.state = state;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getMyID() {
        return myID;
    }

    public void setMyID(String myID) {
        this.myID = myID;
    }

    public String getTargetID() {
        return targetID;
    }

    public void setTargetID(String targetID) {
        this.targetID = targetID;
    }

    public String getGonderenAdi() {
        return gonderenAdi;
    }

    public void setGonderenAdi(String gonderenAdi) {
        this.gonderenAdi = gonderenAdi;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIlanID() {
        return ilanID;
    }

    public void setIlanID(String ilanID) {
        this.ilanID = ilanID;
    }
}