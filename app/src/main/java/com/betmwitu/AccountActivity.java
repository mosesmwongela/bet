package com.betmwitu;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.betmwitu.app.AppController;
import com.betmwitu.db.UserSessionManager;
import com.betmwitu.model.Account;
import com.betmwitu.model.AccountListAdapter;
import com.betmwitu.util.ConnectionDetector;
import com.betmwitu.util.SystemAlertWindowService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by turnkey on 12/17/2016.
 */

public class AccountActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private ConnectionDetector cd;
    private UserSessionManager session;
    private String TAG = "AccountActivity";
    private Button btnTopUpNow;
    private int count = 0;
    private Boolean DOING_REFRESH_ANIM = false;

    private Menu mymenu;

    private static final String account_url = "http://sikumojaventures.com/json/account.json";
    private List<Account> accountList = new ArrayList<Account>();
    private AccountListAdapter adapter;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        cd = new ConnectionDetector(getApplicationContext());
        session = new UserSessionManager(getApplicationContext());

        btnTopUpNow = (Button) findViewById(R.id.btnTopUpNow);
        btnTopUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topUpActivity();
            }
        });

        listView = (ListView) findViewById(R.id.list);
        adapter = new AccountListAdapter(this, accountList);
        listView.setAdapter(adapter);

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
       // actionBar.setSubtitle(session.getUserName());

        // this thread delays for 55ms(100 ms just to be safe) so that
        // onCreateOptionsMenu happens first
        // to avoid null pointer on the menu
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshAccount();
            }
        }, 100);
    }

    private void refreshAccount() {
        if(!DOING_REFRESH_ANIM)
            refreshAnim();

        JsonArrayRequest accountReq = new JsonArrayRequest(account_url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        stopRefreshAnim();

                        accountList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Account acc = new Account();

                                acc.setTrans_id(obj.getString("trans_id"));
                                acc.setTrans_type(obj.getString("trans_type"));
                                acc.setTrans_amount(obj.getString("trans_amount"));
                                acc.setDesc(obj.getString("desc"));

                                accountList.add(acc);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Sorting
//                        Collections.sort(accountList, new Comparator<Account>() {
//                            @Override
//                            public int compare(Account acc1, Account acc2) {
//
//                                return acc1.getTrans_id().compareTo(acc2.getTrans_id());
//                            }
//                        });

                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                stopRefreshAnim();
            }
        });

        AppController.getInstance().addToRequestQueue(accountReq);
    }

    public void topUpActivity() {
        try {
            Intent stk = getPackageManager().getLaunchIntentForPackage("com.android.stk");
            if (stk != null)
                startActivity(stk);
            Intent intent = new Intent(this, SystemAlertWindowService.class);
            startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        if (count == 1) {
            if (isMyServiceRunning(SystemAlertWindowService.class)) {
                Intent intent = new Intent(this, SystemAlertWindowService.class);
                stopService(intent);
            }
            count = 0;
            finish();
        } else {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account, menu);
        mymenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_refresh) {
            refreshAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshAnim(){
        MenuItem menuItem = mymenu.findItem(R.id.action_refresh);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);
        menuItem.setActionView(iv);
        DOING_REFRESH_ANIM = true;
    }

    public void stopRefreshAnim()
    {
        MenuItem m = mymenu.findItem(R.id.action_refresh);
        if(m.getActionView()!=null)
        {
            m.getActionView().clearAnimation();
            m.setActionView(null);
            DOING_REFRESH_ANIM = false;
        }
    }


}
