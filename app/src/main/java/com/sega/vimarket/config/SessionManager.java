package com.sega.vimarket.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sega.vimarket.model.Utils;

/**a
 * Created by Sega on 8/1/2016.
 */
public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    private SharedPreferences pref,colorful;
    private SharedPreferences.Editor editor;
    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_USERID = "userid";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_UNIQUEID = "uniqueid";
    private static final String KEY_UNIQUEPIC = "uniquepic";
    public SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        colorful = context.getSharedPreferences((Utils.PREFERENCE_KEY),PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, int userid, String username,String uniqueid,String userpic) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putInt(KEY_USERID, userid);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_UNIQUEID, uniqueid);
        editor.putString(KEY_UNIQUEPIC, userpic);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }
    public void setCurrency(Double currency){
        editor.putString("currency", Double.toString(currency));
        editor.commit();
    }
    public Double getCurrency(){
        return Double.valueOf(pref.getString("currency","1"));
    }
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public void deleteLogin(){
        pref.edit().clear().apply();

    }
    public void setColor(int color,int color2){
        editor.putInt("color",color);
        editor.putInt("color2",color2);
        editor.commit();
    }
    public int getColor() {
        return pref.getInt("color", -1);
    }
    public void setLastpage(String page){
        editor.putString("lastpage", page);
        editor.commit();
    }
    public void setDefaultPage(){
        editor.remove("lastpage").commit();
    }
    public String getLastpage() {
        return pref.getString("lastpage","");
    }
    public int getColor2() {
        return pref.getInt("color2", -1);
    }
    public int getLoginId() {
        return pref.getInt(KEY_USERID, -1);
    }
    public String getLoginName() {
        return pref.getString(KEY_USERNAME,"");
    }
    public String getLoginPic() {
        return pref.getString(KEY_UNIQUEPIC,"");
    }
}