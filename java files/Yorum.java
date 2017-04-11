package com.example.yusuf.mobilnakliyeyc;

/**
 * Created by yusuf on 1.03.2017.
 */

public class Yorum {
    private String ID, yorum, tarih, yildiz, hedef_ID;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = yorum;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getYildiz() {
        return yildiz;
    }

    public void setYildiz(String yildiz) {
        this.yildiz = yildiz;
    }

    public String getHedef_ID() {
        return hedef_ID;
    }

    public void setHedef_ID(String hedef_ID) {
        this.hedef_ID = hedef_ID;
    }
}
