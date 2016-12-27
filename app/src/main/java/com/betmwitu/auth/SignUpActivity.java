package com.betmwitu.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.betmwitu.MainActivity;
import com.betmwitu.R;
import com.betmwitu.util.Config;
import com.betmwitu.util.ConnectionDetector;
import com.betmwitu.util.JSONParser;
import com.betmwitu.db.UserSessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends ActionBarActivity implements OnClickListener{

    private EditText etFullName, etPhone, etPass;
    private Button btnSingUp;
    private Button btnLogin;
    private String strFullName=null, strPhone=null, strPass=null, strReferee=null;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    UserSessionManager session;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        etFullName = (EditText)findViewById(R.id.etFullName);
        etPhone = (EditText)findViewById(R.id.etPhone);
        etPass = (EditText)findViewById(R.id.etPass);
        btnSingUp = (Button)findViewById(R.id.btnSingUp);
        btnLogin = (Button)findViewById(R.id.btnLogin);

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
                SignInEvent();
            }
        });
    }

    public void loginButtonEvent() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }


    public void SignInEvent() {
        strFullName = etFullName.getText().toString();
        strPhone = etPhone.getText().toString();
        strPass = etPass.getText().toString();

        int errors = 0;
        if(etFullName.equals("")||(etFullName==null)){
            errors++;
            errorToast("Enter your full name");
        }
        if(etFullName.length()<3&&(errors==0)){
            errors++;
            errorToast("Your name should be least 3 characters long");
        }
        if(etFullName.length()>40&&(errors==0)){
            errors++;
            errorToast("Your name can have a maximum of 40 characters");
        }
        if((strPhone.equalsIgnoreCase("")||(strPhone==null))&&(errors==0)){
            errors++;
            errorToast("Enter your phone number");
        }
        if((errors==0)&&(strPhone.length()!=10)){
            errors++;
            errorToast("Your phone number should be have 10 digits");
        }

        if((errors==0)&&(strPass.length()<6)){
            errors++;
            errorToast("Your password should be at least 6 characters");
        }

        boolean isInternetPresent = cd.isConnectingToInternet();
        if((errors==0)&&(!isInternetPresent)){
            errors++;
            errorToast("No internet connection");
        }

        if(errors==0){
            new CreateUser().execute();
        }
    }

    @Override
    public void onClick(View v) {

    }

    class CreateUser extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setTitle("Signing up");
            pDialog.setMessage("Hi, "+strFullName+". please wait as we create your account...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("fname", strFullName));
                params.add(new BasicNameValuePair("phone", strPhone));
                params.add(new BasicNameValuePair("pass", strPass));

                JSONObject json = jsonParser.makeHttpRequest(Config.SIGNUP_URL, "POST", params);

                success = json.getInt(Config.TAG_SUCCESS);
                if (success == 1) {

                    // Creating user login session
                    session.createUserLoginSession(strFullName,strPhone);

                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
                return json.getString(Config.TAG_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(SignUpActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.activity_signup_actions, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_help:
//                help();
//                return true;
//            case R.id.action_signin:
//                Intent signin = new Intent(SignUpActivity.this, LoginActivity.class);
//                signin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                signin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(signin);
//                finish();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void help(){
        AlertDialog.Builder alert = new AlertDialog.Builder(SignUpActivity.this);
        alert.setTitle("How to create an account");
        alert.setMessage("Fill in the form with your details. Enter your full name, phone number and create a new password and tap sign up. All fields are compulsory.");
        alert.setPositiveButton("Got it",null);
        alert.show();
    }



    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }


    private void errorToast(String tst){
        Toast.makeText(SignUpActivity.this, tst, Toast.LENGTH_SHORT).show();
    }
}
