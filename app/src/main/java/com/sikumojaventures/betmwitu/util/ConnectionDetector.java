package com.sikumojaventures.betmwitu.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mwongela on 12/15/16.
 */
public class ConnectionDetector {

    private static final String TAG = "ConnectionDetector";
    private Context _context;
    Handler handler;


    public ConnectionDetector(Context context) {
        this._context = context;
        handler = new Handler();
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public boolean hasActiveInternetConnection() {
        if (isConnectingToInternet2()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }

    public boolean isConnectingToInternet2() {
        final boolean[] internet_works = new boolean[1];
        Thread thread = new Thread() {
            @Override
            public void run() {
                internet_works[0] = hasActiveInternetConnection();
            }
        };
        thread.start();

        return internet_works[0];
    }
}