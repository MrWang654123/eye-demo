package com.cheersmind.smartbrain.module.login;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cheersmind.smartbrain.main.QSApplication;
import com.cheersmind.smartbrain.main.entity.ChildInfoEntity;

/**
 * Created by goodm on 2017/4/15.
 */
public class UCManager {
    private String acccessToken;
    //private Date expiresAt;
    private String refreshToken;
    private String macKey;
    private long userId;
    private String nickName;
    private String realName;

    private ChildInfoEntity defaultChild;

    private static UCManager instance;
    private UCManager (){}

    public static UCManager getInstance() {
        if (instance == null) {
            instance = new UCManager();
        }
        return instance;
    }

    public long getDefaultChildId() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
        return pref.getLong("default_child_id_"+getUserId(),0);
    }

    public void setDefaultChildId(long defaultChildId) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(QSApplication.getContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("default_child_id_"+getUserId(), defaultChildId);
        editor.commit();
    }

    //private long defaultChildId;


    public ChildInfoEntity getDefaultChild() {
        return defaultChild;
    }

    public void setDefaultChild(ChildInfoEntity defaultChild) {
        this.defaultChild = defaultChild;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public static void setInstance(UCManager instance) {
        UCManager.instance = instance;
    }

    public String getAcccessToken () {
        if (this.acccessToken == null ){
            System.exit(0);
        }
        return this.acccessToken;
    }

    public String getRefreshToken () {
        return this.refreshToken;
    }

    public  long getUserId() {
        return this.userId;
    }

    public void setAcccessToken (String accessToken) {
        this.acccessToken = accessToken;
    }

    public void setRefreshToken (String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setUserId (long userId) {
        this.userId = userId;
    }

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }

    public String getMacKey() {
        if (this.macKey == null ){
            System.exit(0);
        }
        return this.macKey;
    }

    public void clearToken(){
        this.acccessToken = "";
        this.refreshToken = "";
        this.macKey = "";
        this.userId  = 0;
        this.nickName = "";
        this.realName = "";
    }
}
