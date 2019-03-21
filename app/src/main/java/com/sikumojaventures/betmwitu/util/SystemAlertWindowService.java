package com.sikumojaventures.betmwitu.util;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sikumojaventures.betmwitu.R;


/**
 * Created by mwongela on 12/20/16.
 */
public class SystemAlertWindowService extends Service {

    View systemAlertView;

    public SystemAlertWindowService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(getString(R.string.not_yet_implemented));
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        try {
            String tillNumber = "Till number: 560921";
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    300, //width
                    68, //height
                    90,
                    0,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);
            final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            systemAlertView = inflater.inflate(R.layout.system_alert_window_layout, null);
            ImageView img_close = (ImageView) systemAlertView.findViewById(R.id.img_close);
            TextView tv_till_number = (TextView) systemAlertView.findViewById(R.id.tv_till_number);
            tv_till_number.setText(tillNumber);
            img_close.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    systemAlertView.setVisibility(View.GONE);
                    return true;
                }
            });
            params.gravity = Gravity.TOP;
            wm.addView(systemAlertView, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (systemAlertView != null) {
            systemAlertView.setVisibility(View.GONE);
        }
    }
}