package com.betmwitu.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.betmwitu.R;
import com.betmwitu.db.UserSessionManager;
import com.betmwitu.util.Config;
import com.betmwitu.util.ConnectionDetector;
import com.betmwitu.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends ActionBarActivity implements OnClickListener{

    private static final String TAG = "LoginActivity";
    JSONParser jsonParser = new JSONParser();
    ConnectionDetector cd;
    UserSessionManager session;
    private EditText etPhone, etPass;
    private Button btnLogin;
    private Button btnSingUp;
    private String strPhone=null, strPass=null;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new UserSessionManager(getApplicationContext());

        cd = new ConnectionDetector(getApplicationContext());

        etPhone = (EditText)findViewById(R.id.etPhone);
        etPass = (EditText)findViewById(R.id.etPass);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnSingUp = (Button)findViewById(R.id.btnSingUp);

        registerButtonListeners();
    }

    private void registerButtonListeners() {
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButtonEvent();
            }
        });
        btnSingUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpButtonEvent();
            }
        });
    }

    public void signUpButtonEvent() {
        Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(i);
        finish();
    }


    public void loginButtonEvent() {
        strPhone = etPhone.getText().toString();
        strPass = etPass.getText().toString();

        int errors = 0;
        if((strPhone.equalsIgnoreCase("")||(strPhone==null))&&(errors==0)){
            errors++;
            errorToast("Please Enter your phone number");
        }
        if((errors==0)&&(strPhone.length()!=10)){
            errors++;
            errorToast("Your phone number should be have 10 digits");
        }
        if((strPass.equalsIgnoreCase("")||(strPass==null))&&(errors==0)){
            errors++;
            errorToast("Enter your password");
        }
        if((errors==0)&&(strPass.length()<6)){
            errors++;
            errorToast("Wrong password");
        }

        boolean isInternetPresent = cd.isConnectingToInternet();
        if((errors==0)&&(!isInternetPresent)){
            errors++;
            errorToast("No internet connection");
        }

        if(errors==0){
            new AttemptLogin().execute();
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void help() {
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        alert.setTitle("How to login");
        alert.setMessage("First, enter the phone number that you used at registration. Second, enter the password that you used during registration." +
                "Don't have and account? Click on create account to register one.");
        alert.setPositiveButton("Got it", null);
        alert.show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.activity_login_actions, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_help:
//                help();
//                return true;
//            case R.id.action_signup:
//                Intent signup = new Intent(LoginActivity.this, SignUpActivity.class);
//                signup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                signup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(signup);
//                finish();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void errorToast(String tst) {
        Toast.makeText(LoginActivity.this, tst, Toast.LENGTH_SHORT).show();
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        String message = null;
        int success = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setTitle("Attempting Login");
            pDialog.setMessage("please wait as we try to log you in...");

//            LayoutInflater inflater =  getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.dialogue_layout, null);
//            pDialog.setView(dialogView);
//            TextView dialogue_heading = (TextView) dialogView.findViewById(R.id.dialogue_heading);
//            TextView dialogue_message = (TextView) dialogView.findViewById(R.id.dialogue_message);
//            dialogue_message.setText("Attempting Login");
//            dialogue_message.setText("please wait as we try to log you in...");

            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            String user_name;
            String account_balance;
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phone", strPhone));
                params.add(new BasicNameValuePair("pass", strPass));

                JSONObject json = jsonParser.makeHttpRequest(Config.LOGIN_URL, "POST", params);
                Log.e(TAG, json.toString());

                success = json.getInt(Config.TAG_SUCCESS);
                message = json.getString(Config.TAG_MESSAGE);

                if (success == 1) {
                    user_name = json.getString(Config.TAG_USER_NAME);
                    account_balance = json.getString("account_balance");
                    message = null;
                    session.createUserLoginSession(user_name, strPhone);
                    session.updateAccountBalance(account_balance);
//                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(i);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return message;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

}
