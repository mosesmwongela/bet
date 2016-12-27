package com.betmwitu.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mwongela on 12/15/16.
 */
public class UserSessionManager {

    public static final String KEY_USER_NAME = "username";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_ACCOUNT_BALANCE = "account_balance";
    public static final String KEY_ACCOUNT_BALANCE_INT = "account_balance_int";
    public static final String KEY_FIRST_RUN = "firstRun";
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

    public void updateAccountBalance(String account_balance) {
        editor.putString(KEY_ACCOUNT_BALANCE, "Ksh " + account_balance);
        editor.putString(KEY_ACCOUNT_BALANCE_INT, account_balance);
        editor.commit();
    }

    public void createUserLoginSession(String username, String phone){
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USER_NAME, username);
        editor.putString(KEY_PHONE, phone);
        editor.commit();
    }

    public void logFirstRun(Boolean isFirstRun){
        editor.putBoolean(KEY_FIRST_RUN, isFirstRun);
        editor.commit();
    }

    public boolean isFirstRun(){
        return pref.getBoolean(KEY_FIRST_RUN, true);
    }

    public String getUserName(){
        return pref.getString(KEY_USER_NAME, null);
    }

    public String getAccountBalance() {
        return pref.getString(KEY_ACCOUNT_BALANCE, "0.00");
    }

    public String getAccountBalanceInt() {
        return pref.getString(KEY_ACCOUNT_BALANCE_INT, "0");
    }

    public String getPhone(){
        return pref.getString(KEY_PHONE, "");
    }

    public void logout() {
        editor.putBoolean(IS_USER_LOGIN, false);
        editor.putString(KEY_USER_NAME, null);
        editor.putString(KEY_PHONE, null);
        editor.commit();
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        logFirstRun(true);
    }

    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}
