package com.farminfinity.farminfinity.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.farminfinity.farminfinity.Activities.LoginActivity;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;

    SharedPreferences.Editor editor;

    Context _context;

    private final int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "InfinityPref";

    private static final String IS_LOGIN = "IsLoggedIn";

    private static final String KEY_TOKEN = "Token";

    private static final String KEY_USER_TYPE = "UserType";

    private static final String KEY_USER_SESSION = "Session";

    private static final String KEY_PREF_LANG = "Language";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String token, String userType, String session){
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_TOKEN, token);

        editor.putString(KEY_USER_TYPE, userType);

        editor.putString(KEY_USER_SESSION, session);

        editor.commit();
    }

    public void createPreferredLanguage(String language){
        editor.putString(KEY_PREF_LANG, language);
        editor.commit();
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
        user.put(KEY_USER_TYPE, pref.getString(KEY_USER_TYPE, null));
        user.put(KEY_USER_SESSION, pref.getString(KEY_USER_SESSION, null));

        return user;
    }

    public String getPreferredLanguage() {
        return pref.getString(KEY_PREF_LANG, null);
    }

    public void logoutUser(){
        editor.putBoolean(IS_LOGIN, false);
        //editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
