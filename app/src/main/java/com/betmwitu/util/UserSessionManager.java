package com.betmwitu.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mwongela on 12/15/16.
 */
public class UserSessionManager {

    public static final String KEY_USER_NAME = "username";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_ACC_BALANCE = "account_balance";
    private static final String PREFER_NAME = "1315190519";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public UserSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String username, String phone){
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USER_NAME, phone);
        editor.putString(KEY_PHONE, phone);
        editor.commit();
    }

    public void createAccBalanceSession(String account_balance){
        editor.putString(KEY_ACC_BALANCE, account_balance);
        editor.commit();
    }

    public String getAccountBalance(){
        return pref.getString(KEY_ACC_BALANCE, null);
    }

    public String getUserName(){
        return pref.getString(KEY_USER_NAME, null);
    }

    public String getPhone(){
        return pref.getString(KEY_PHONE, null);
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

    }

    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
