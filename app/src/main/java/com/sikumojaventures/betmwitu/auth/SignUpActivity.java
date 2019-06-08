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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sikumojaventures.betmwitu.R;
import com.sikumojaventures.betmwitu.db.UserSessionManager;
import com.sikumojaventures.betmwitu.util.Config;
import com.sikumojaventures.betmwitu.util.ConnectionDetector;
import com.sikumojaventures.betmwitu.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity implements OnClickListener{

    JSONParser jsonParser = new JSONParser();
    UserSessionManager session;
    ConnectionDetector cd;
    private EditText etEmail, etPhone, etPass;
    private Button btnSingUp;
    private Button btnLogin;
    private String strEmail=null, strPhone=null, strPass=null, strReferee=null;
    private ProgressDialog pDialog;

    private AssetManager am = null;
    private Typeface typeface_bold = null;
    private Typeface typeface_regular = null;
    private SpannableString subtitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        am = getAssets();
        typeface_bold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "bariol_bold_webfont.ttf"));
        typeface_regular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "bariol_regular_webfont.ttf"));

        subtitle = new SpannableString("Create an account with us");
        subtitle.setSpan(typeface_regular, 0, subtitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        etEmail = (EditText)findViewById(R.id.etEmail);
        etEmail.setTypeface(typeface_regular);
        etPhone = (EditText)findViewById(R.id.etPhone);
        etPhone.setTypeface(typeface_regular);
        etPass = (EditText)findViewById(R.id.etPass);
        etPass.setTypeface(typeface_regular);
        btnSingUp = (Button)findViewById(R.id.btnSingUp);
        btnSingUp.setTypeface(typeface_regular);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setTypeface(typeface_regular);

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
                SignInEvent();
            }
        });
    }

    public void loginButtonEvent() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
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


    public void SignInEvent() {
        strEmail = etEmail.getText().toString().trim();
        strPhone = etPhone.getText().toString().trim();
        strPass = etPass.getText().toString().trim();

        int errors = 0;
        if(strEmail.equals("")||(strEmail==null)){
            errors++;
            errorToast("Enter your email address");
        }

        if (!validateEmail(strEmail)&&(errors==0)) {
            errorToast("Please enter a valid email address");
            errors++;
        }

        if((strPhone.equalsIgnoreCase("")||(strPhone==null))&&(errors==0)){
            errors++;
            errorToast("Enter your phone number");
        }

        Pattern pattern = Pattern.compile("^(?:254|\\+254|0)?(7(?:(?:[12][0-9])|(?:0[0-8])|(?:9[0-9])|(?:4[0-8]))[0-9]{6})$");
        Matcher matcher = pattern.matcher(strPhone);
        if (matcher.matches()) {
            strPhone = "0" + matcher.group(1);
        }else{
            errors++;
            android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(SignUpActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialogue_layout, null);
            builder1.setView(dialogView);
            TextView dialogue_heading = (TextView) dialogView.findViewById(R.id.dialogue_heading);
            TextView dialogue_message = (TextView) dialogView.findViewById(R.id.dialogue_message);
            dialogue_heading.setText("Invalid phone number");
            dialogue_heading.setTypeface(typeface_bold);
            dialogue_message.setText("Ensure you typed in your phone number correctly. We only accept Safaricom phone numbers because we use an M-PESA till number.");
            dialogue_heading.setTypeface(typeface_regular);
            builder1.setCancelable(true);
            builder1.setPositiveButton("I will fix this", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            android.support.v7.app.AlertDialog alert11 = builder1.create();
            alert11.show();
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

    public void help() {
        AlertDialog.Builder alert = new AlertDialog.Builder(SignUpActivity.this);
        alert.setTitle("How to create an account");
        alert.setMessage("Fill in the form with your details. Enter your full name, phone number and create a new password and tap sign up. All fields are compulsory.");
        alert.setPositiveButton("Got it", null);
        alert.show();
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

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void errorToast(String tst) {
        Toast.makeText(SignUpActivity.this, tst, Toast.LENGTH_SHORT).show();
    }

    class CreateUser extends AsyncTask<String, String, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignUpActivity.this);
           // pDialog.setTitle("Signing up");
            pDialog.setMessage("connecting to server...");

//            LayoutInflater inflater =  getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.dialogue_layout, null);
//            pDialog.setView(dialogView);
//            TextView dialogue_heading = (TextView) dialogView.findViewById(R.id.dialogue_heading);
//            TextView dialogue_message = (TextView) dialogView.findViewById(R.id.dialogue_message);
//            dialogue_message.setText("Signing up");
//            dialogue_message.setText("Hi, "+strFullName+". please wait as we create your account...");

            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("fname", ""));
                params.add(new BasicNameValuePair("phone", strPhone));
                params.add(new BasicNameValuePair("pass", strPass));
                params.add(new BasicNameValuePair("email", strEmail));

                JSONObject json = jsonParser.makeHttpRequest(Config.SIGNUP_URL, "POST", params);

                success = json.getInt(Config.TAG_SUCCESS);
                if (success == 1) {

                    // Creating user login session
                    session.createUserLoginSession(strEmail,strPhone);

//                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(i);
                    finish();
                    return null;
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
}
