package com.betmwitu;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.betmwitu.db.UserSessionManager;
import com.betmwitu.model.Account;
import com.betmwitu.model.AccountListAdapter;
import com.betmwitu.util.Config;
import com.betmwitu.util.ConnectionDetector;
import com.betmwitu.util.JSONParser;
import com.betmwitu.util.SystemAlertWindowService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by turnkey on 12/17/2016.
 */

public class AccountActivity extends AppCompatActivity {

    private static final String account_url = "http://sikumojaventures.com/betmwitu/get_transactions.php";
    private static final String trans_code_url = "http://sikumojaventures.com/betmwitu/transcode.php";
    TextView accountbalance;
    private ActionBar actionBar;
    private ConnectionDetector cd;
    private UserSessionManager session;
    private String TAG = "AccountActivity";
    private Button btnTopUpNow;
    private int count = 0;
    private Boolean DOING_REFRESH_ANIM = false;
    private Menu mymenu;
    private List<Account> accountList = new ArrayList<Account>();
    private AccountListAdapter adapter;
    private EditText etTransCode;
    private JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        cd = new ConnectionDetector(getApplicationContext());
        session = new UserSessionManager(getApplicationContext());

        LinearLayout profile_card = (LinearLayout) findViewById(R.id.profile_card);
        TextView username = (TextView) findViewById(R.id.usernameTxt);
        TextView phonenumber = (TextView) findViewById(R.id.phonenumberTxt);
        accountbalance = (TextView) findViewById(R.id.accountbalanceTxt);
        etTransCode = (EditText) findViewById(R.id.etTransCode);
        Button btnSubmitTransCode = (Button) findViewById(R.id.btnSubmitTransCode);

        if (!session.isUserLoggedIn()) {
            profile_card.setVisibility(View.GONE);
        } else {
            username.setText(session.getUserName());
            phonenumber.setText(session.getPhone());
            accountbalance.setText(session.getAccountBalance());
        }

        btnTopUpNow = (Button) findViewById(R.id.btnTopUpNow);
        btnTopUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topUpActivity();
            }
        });

        btnSubmitTransCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTransactionCode();
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
                if (cd.isConnectingToInternet()) {
                    new getTransactions().execute();
                } else {
                    Toast.makeText(AccountActivity.this, "Failed to connect to the internet", Toast.LENGTH_LONG).show();
                }
            }
        }, 100);
    }

    private void sendTransactionCode() {
        String transCode = etTransCode.getText().toString();
        if (transCode == null || transCode.length() < 10) {
            Toast.makeText(AccountActivity.this, "Enter a valid M-Pesa code", Toast.LENGTH_LONG).show();
            return;
        }
        if (cd.isConnectingToInternet()) {
            new sendTransCode().execute(transCode);
        } else {
            Toast.makeText(AccountActivity.this, "Cannot connect to the internet", Toast.LENGTH_LONG).show();
            return;
        }
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

//    private void refreshAccount() {
//        if(!DOING_REFRESH_ANIM)
//            refreshAnim();
//
//        JsonArrayRequest accountReq = new JsonArrayRequest(account_url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d(TAG, response.toString());
//                        stopRefreshAnim();
//
//                        accountList.clear();
//
//                        for (int i = 0; i < response.length(); i++) {
//                            try {
//
//                                JSONObject obj = response.getJSONObject(i);
//                                Account acc = new Account();
//
//                                acc.setTrans_id(obj.getString("trans_id"));
//                                acc.setTrans_type(obj.getString("trans_type"));
//                                acc.setTrans_amount(obj.getString("trans_amount"));
//                                acc.setDesc(obj.getString("desc"));
//
//                                accountList.add(acc);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        // Sorting
////                        Collections.sort(accountList, new Comparator<Account>() {
////                            @Override
////                            public int compare(Account acc1, Account acc2) {
////
////                                return acc1.getTrans_id().compareTo(acc2.getTrans_id());
////                            }
////                        });
//
//                        adapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                stopRefreshAnim();
//            }
//        });
//
//        AppController.getInstance().addToRequestQueue(accountReq);
//    }

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
            if (cd.isConnectingToInternet()) {
                new getTransactions().execute();
            }
            return true;
        }

        if (id == R.id.action_logout) {
            try {
                session.logout();
                Toast.makeText(AccountActivity.this, "Logged out", Toast.LENGTH_LONG).show();
                Intent i = new Intent(AccountActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    class sendTransCode extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AccountActivity.this);
            pDialog.setMessage("Validating m-pesa transaction code");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            try {

                String transCode = args[0];

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("transCode", transCode));
                params.add(new BasicNameValuePair("phone", session.getPhone()));

                JSONObject json = jsonParser.makeHttpRequest(trans_code_url, "POST", params);

                success = json.getInt(Config.TAG_SUCCESS);
                if (success == 1) {
                    String account_balance = json.getString("account_balance");
                    session.updateAccountBalance(account_balance);
                }
                return json.getString(Config.TAG_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(AccountActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
            accountbalance.setText(session.getAccountBalance());
        }
    }

    class getTransactions extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!DOING_REFRESH_ANIM)
                refreshAnim();

        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phone", session.getPhone()));
                JSONObject json = jsonParser.makeHttpRequest(account_url, "POST", params);
                success = json.getInt(Config.TAG_SUCCESS);

                if (success == 1) {
                    accountList.clear();
                    JSONArray transactions = json.getJSONArray("transactions");
                    for (int i = 0; i < transactions.length(); i++) {
                        try {
                            JSONObject obj = transactions.getJSONObject(i);
                            Account acc = new Account();

                            // acc.setTrans_id(obj.getString("trans_id"));
                            acc.setTrans_type(obj.getString("trans_type"));
                            acc.setTrans_amount(obj.getString("trans_amount"));
                            acc.setDesc(obj.getString("trans_desc"));

                            accountList.add(acc);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return json.getString(Config.TAG_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            if (file_url != null) {
                //  Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
            adapter.notifyDataSetChanged();
            stopRefreshAnim();
        }
    }


}
