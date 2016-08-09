package com.bcg.loginexample;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    public Settings(Context ctx, String email, String userid, String token, String avatar_url) {
        this.ctx = ctx;
        this.email = email;
        this.userid = userid;
        this.token = token;
        this.avatar_url = avatar_url;

        saveSettings(email,userid,token,avatar_url);
    }

    public Settings(Context ctx) {
        this.ctx = ctx;

        }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public Context ctx;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String email;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String userid;
    public String token;
    public String avatar_url;


    public void saveSettings (String email, String userid, String token, String avatar_url){
        SharedPreferences prefs =ctx.getSharedPreferences("pref", Context.MODE_PRIVATE);
        if(email!=null)
            prefs.edit().putString("email", email).apply();
        if(userid!=null)
            prefs.edit().putString("userid",  userid).apply();
        if(token!=null)
            prefs.edit().putString("token",  token).apply();
        if(avatar_url!=null)
            prefs.edit().putString("avatar_url", avatar_url).apply();


    }

    public Settings getSettings () {
        SharedPreferences prefs = ctx.getSharedPreferences("pref", Context.MODE_PRIVATE);

        Settings settings=new Settings(ctx, prefs.getString("email",""),
                prefs.getString("userid",""),
                prefs.getString("token",""),
                prefs.getString("avatar_url",""));
        return settings;
    }
};
