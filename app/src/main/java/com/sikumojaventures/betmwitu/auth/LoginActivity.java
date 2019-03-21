package com.sikumojaventures.betmwitu.auth;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sikumojaventures.betmwitu.R;
import com.sikumojaventures.betmwitu.db.UserSessionManager;
import com.sikumojaventures.betmwitu.util.Config;
import com.sikumojaventures.betmwitu.util.ConnectionDetector;
import com.sikumojaventures.betmwitu.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "LoginActivity";
    JSONParser jsonParser = new JSONParser();
    ConnectionDetector cd;
    UserSessionManager session;
    private EditText etPhone, etPass;
    private Button btnLogin;
    private Button btnSingUp, btnForgotPass;
    private String strPhone = null, strPass = null, strEmail;
    private ProgressDialog pDialog;

    private AssetManager am = null;
    private Typeface typeface_bold = null;
    private Typeface typeface_regular = null;
    private SpannableString subtitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        am = getAssets();
        typeface_bold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "bariol_bold_webfont.ttf"));
        typeface_regular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "bariol_regular_webfont.ttf"));

        subtitle = new SpannableString("Login");
        subtitle.setSpan(typeface_regular, 0, subtitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        etPhone = (EditText) findViewById(R.id.etPhone);
        etPhone.setTypeface(typeface_regular);
        etPass = (EditText) findViewById(R.id.etPass);
        etPass.setTypeface(typeface_regular);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setTypeface(typeface_regular);
        btnSingUp = (Button) findViewById(R.id.btnSingUp);
        btnSingUp.setTypeface(typeface_regular);
        btnForgotPass = (Button) findViewById(R.id.btnForgotPass);
        btnForgotPass.setTypeface(typeface_regular);

        registerButtonListeners();

        actionBarSetup();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void actionBarSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            android.support.v7.app.ActionBar ab = getSupportActionBar();
            ab.setSubtitle(subtitle);
        }
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
        btnForgotPass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remindMemYPass();
            }
        });
    }

    public void remindMemYPass() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.reset_pass_dialogue, null);
        builder1.setView(dialogView);
        final EditText etEmailTxt = (EditText) dialogView.findViewById(R.id.etEmailTxt);
        etEmailTxt.setTypeface(typeface_regular);

        builder1.setCancelable(true);
        builder1.setPositiveButton("Send me my password", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                strEmail = etEmailTxt.getText().toString();
                if ((strEmail.equalsIgnoreCase("") || (strEmail == null))) {
                    errorToast("Please enter your email address");
                    return;
                }
                if (!validateEmail(strEmail)) {
                    errorToast("Please enter a valid email address");
                    return;
                }

                boolean isInternetPresent = cd.isConnectingToInternet();
                if (!isInternetPresent) {
                    errorToast("No internet connection");
                }

                new RemindPassword().execute();
            }
        });
        builder1.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void signUpButtonEvent() {
        Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(i);
        finish();
    }

    public boolean validateEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void loginButtonEvent() {
        strPhone = etPhone.getText().toString();
        strPass = etPass.getText().toString();

        int errors = 0;
        if ((strPhone.equalsIgnoreCase("") || (strPhone == null)) && (errors == 0)) {
            errors++;
            errorToast("Please Enter your phone number");
        }
        Pattern pattern = Pattern.compile("^(?:254|\\+254|0)?(7(?:(?:[12][0-9])|(?:0[0-8])|(?:9[0-2]))[0-9]{6})$");
        Matcher matcher = pattern.matcher(strPhone);
        if (matcher.matches()) {
            strPhone = "0" + matcher.group(1);
        }
        if ((strPass.equalsIgnoreCase("") || (strPass == null)) && (errors == 0)) {
            errors++;
            errorToast("Enter your password");
        }
        if ((errors == 0) && (strPass.length() < 6)) {
            errors++;
            errorToast("Wrong password");
        }

        boolean isInternetPresent = cd.isConnectingToInternet();
        if ((errors == 0) && (!isInternetPresent)) {
            errors++;
            errorToast("No internet connection");
        }

        if (errors == 0) {
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

    class RemindPassword extends AsyncTask<String, String, String> {

        String message = null;
        int success = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Looking for your password...");
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
                params.add(new BasicNameValuePair("email", strEmail));

                JSONObject json = jsonParser.makeHttpRequest(Config.FORGORT_PASS_URL, "POST", params);
                Log.e(TAG, json.toString());

                success = json.getInt(Config.TAG_SUCCESS);
                message = json.getString(Config.TAG_MESSAGE);

                if (success == 1) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return message;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        String message = null;
        int success = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            //pDialog.setTitle("Attempting Login");
            pDialog.setMessage("attempting login...");

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
            if (file_url != null) {
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}