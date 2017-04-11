package com.example.yusuf.mobilnakliyeyc.Mesajlarim;

/**
 * Created by yusuf on 1.03.2017.
 */

public class GelenKutusu {
    private String userName = "";
    private String userID = "";
    private boolean okunayanMesaj = false;

    public GelenKutusu(String userName, String userID, boolean okunayanMesaj) {
        this.userName = userName;
        this.userID = userID;
        this.okunayanMesaj = okunayanMesaj;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isOkunayanMesaj() {
        return okunayanMesaj;
    }

    public void setOkunayanMesaj(boolean okunayanMesaj) {
        this.okunayanMesaj = okunayanMesaj;
    }
}
