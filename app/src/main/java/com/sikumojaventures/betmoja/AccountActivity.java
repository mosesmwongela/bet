package com.sikumojaventures.betmoja;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.sikumojaventures.betmoja.util.SystemAlertWindowService;

/**
 * Created by turnkey on 12/17/2016.
 */

public class AccountActivity extends AppCompatActivity {

    private String TAG = "AccountActivity";

    private Button btnTopUpNow;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        btnTopUpNow = (Button)findViewById(R.id.btnTopUpNow);
        btnTopUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topUpActivity();
            }
        });
    }

    public void topUpActivity(){
        try{
            Intent stk = getPackageManager().getLaunchIntentForPackage("com.android.stk");
            if (stk != null)
                startActivity(stk);
            Intent intent = new Intent(this, SystemAlertWindowService.class);
            startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        if(count == 1) {
            if(isMyServiceRunning(SystemAlertWindowService.class)){
                Intent intent = new Intent(this, SystemAlertWindowService.class);
                stopService(intent);
            }
            count=0;
            finish();
        }
        else {
            Intent i = new Intent(AccountActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            count++;
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
