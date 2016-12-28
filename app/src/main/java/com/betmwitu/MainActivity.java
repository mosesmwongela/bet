package com.betmwitu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.betmwitu.app.AppController;
import com.betmwitu.auth.LoginActivity;
import com.betmwitu.db.UserSessionManager;
import com.betmwitu.model.CustomListAdapter;
import com.betmwitu.model.Dates;
import com.betmwitu.model.Tip;
import com.betmwitu.util.Config;
import com.betmwitu.util.ConnectionDetector;
import com.betmwitu.util.JSONParser;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.notice.Notice;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


public class MainActivity extends AppCompatActivity {

    private static final String tip_url = "http://www.sikumojaventures.com/betmwitu/get_tips.php";
    private static final String date_url = "http://sikumojaventures.com/betmwitu/get_dates.php";
    private static final String buytip_url = "http://sikumojaventures.com/betmwitu/buytip.php";
    private static final String lastseen_url = "http://sikumojaventures.com/betmwitu/lastseen.php";
    ConnectionDetector cd;
    UserSessionManager session;
    //date to query tips
    String dateParam;
    JSONParser jsonParser = new JSONParser();
    boolean doubleBackToExitPressedOnce = false;
    private String TAG = "MainActivity";
    private List<Tip> tipList = new ArrayList<Tip>();
    private List<Dates> dateList = new ArrayList<Dates>();
    private List<Dates> dateList_Orig = new ArrayList<Dates>();
    private ListView listView;
    private CustomListAdapter adapter;
    private Spinner dateSpinner;
    private Boolean DOING_REFRESH_ANIM = false;
    private Menu mymenu;
    private ProgressDialog pDialog;
    private String IMEI, MANUFACTURER, MODEL, ANDROID_VERSION, APP_VERSION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cd = new ConnectionDetector(getApplicationContext());
        session = new UserSessionManager(getApplicationContext());

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, tipList);
        listView.setAdapter(adapter);
        dateSpinner = (Spinner) findViewById(R.id.datespinner);

        getPhoneDetails();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tv_result = (TextView) view.findViewById(R.id.result);
                TextView tv_tip_id = (TextView) view.findViewById(R.id.tip_id);
                TextView tv_tip_price = (TextView) view.findViewById(R.id.tip_price);
                TextView tv_home_away = (TextView) view.findViewById(R.id.home_away);
                TextView tv_prediction_odd = (TextView) view.findViewById(R.id.prediction_odd);
                TextView tv_date_kick_off = (TextView) view.findViewById(R.id.date_kick_off);

                String result = tv_result.getText().toString();

                if (result.equalsIgnoreCase("Share Tip")) {

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat df2 = new SimpleDateFormat("E dd, MMM");
                    Date startDate;
                    String newDateString = null;
                    try {
                        startDate = df.parse(dateParam);
                        newDateString = df2.format(startDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String tip =
                            tv_home_away.getText().toString() + "\n" +
                                    newDateString + "\n" +
                                    tv_date_kick_off.getText().toString() + "\n" +
                                    tv_prediction_odd.getText().toString();
                    onShareClick(tip);
                }

                if (result.equalsIgnoreCase("Buy Tip")) {

                    if (session.isUserLoggedIn()) {
                        final String tip_id = tv_tip_id.getText().toString();
                        final String tip_price = tv_tip_price.getText().toString();
                        final String home_away = tv_home_away.getText().toString();

                        if (Integer.parseInt(session.getAccountBalanceInt()) >= Integer.parseInt(tip_price)) {

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setTitle("Buy premium tip");
                            builder1.setMessage("Confirm that you want to buy " + home_away + "'s premium tip @ ksh " + tip_price);
                            builder1.setCancelable(true);
                            builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new buyTip().execute(tip_id, tip_price, home_away);
                                }
                            });
                            builder1.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setTitle("Insufficient account balance");
                            builder1.setMessage("You need to top-up your account with ksh " + tip_price + " to be able to purchase this premium tip.");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton("Top up now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(MainActivity.this, AccountActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            });
                            builder1.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }

                    } else {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        //   builder1.setTitle("You are not loged in");
                        builder1.setMessage("You have to be logged in to purchase a premium tip.");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton("Log me in", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        });
                        builder1.setNegativeButton("I will buy later", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                        // .setIcon(android.R.drawable.ic_dialog_alert)
                    }
                }
            }
        });

        if (session.isFirstRun()) {
            Intent i = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(i);

            session.logFirstRun(false);
        }

        checkForUpdate();

        // this thread delays for 55ms(100 ms just to be safe) so that
        // onCreateOptionsMenu happens first
        // to avoid null pointer on the menu
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cd.isConnectingToInternet()) {
                    getthemdata();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    //   builder1.setTitle("You are not loged in");
                    builder1.setMessage("You cannot connect to the internet.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getthemdata();
                        }
                    });
                    builder1.setNegativeButton("Close app", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        }, 100);
    }

    public void getthemdata() {
        if (cd.isConnectingToInternet()) {
            loadDates();
            new getTips().execute();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("You cannot connect to the internet.");
            builder1.setCancelable(false);

            builder1.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    getthemdata();
                }
            });

            builder1.setNegativeButton("Close app", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void popoulateDateSpinner(List<Dates> dateList) {
        List<String> spinnerDate = new ArrayList<String>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df2 = new SimpleDateFormat("E dd, MMM");
        String dateToday = df2.format(Calendar.getInstance().getTime());

        for (int i = 0; i < dateList.size(); i++) {
            Date startDate;
            String newDateString = null;
            try {
                startDate = df.parse(dateList.get(i).getDate());
                newDateString = df2.format(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            spinnerDate.add(newDateString);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerDate);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                int item = dateSpinner.getSelectedItemPosition();
                dateParam = dateList_Orig.get(item).getDate();
                browseTips();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        if (!dateToday.equals(null)) {
            int spinnerPosition = adapter.getPosition(dateToday);
            dateSpinner.setSelection(spinnerPosition);
            try {
                dateParam = dateList_Orig.get(spinnerPosition).getDate();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void browseTips() {
        if (cd.isConnectingToInternet()) {
            new getTips().execute();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("You cannot connect to the internet.");
            builder1.setCancelable(true);

            builder1.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    browseTips();
                }
            });

            builder1.setPositiveButton("Close app", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void loadDates() {
        if (!DOING_REFRESH_ANIM)
            refreshAnim();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("date", "26-12-2016");

        JsonObjectRequest dateReq = new JsonObjectRequest(date_url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        stopRefreshAnim();

                        JSONArray dates = null;

                        dateList.clear();
                        dateList_Orig.clear();

                        try {
                            dates = response.getJSONArray("dates");
                            for (int i = 0; i < dates.length(); i++) {
                                try {
                                    JSONObject obj = dates.getJSONObject(i);
                                    Dates date = new Dates();

                                    date.setDate(obj.getString("date"));

                                    dateList.add(date);
                                    dateList_Orig.add(date);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        popoulateDateSpinner(dateList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                stopRefreshAnim();
            }
        });

        AppController.getInstance().addToRequestQueue(dateReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    public void loadTips() {
//        if (!DOING_REFRESH_ANIM)
//            refreshAnim();
//
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("date", dateParam);
//
//        JsonObjectRequest tipReq = new JsonObjectRequest(Request.Method.POST,  tip_url, new JSONObject(params),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG, response.toString());
//                        stopRefreshAnim();
//
//                        tipList.clear();
//
//                        JSONArray tips = null;
//
//                        try {
//                            tips = response.getJSONArray("tips");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        try {
//                            if (tips != null) {
//                                for (int i = 0; i < tips.length(); i++) {
//
//
//                                    JSONObject obj = tips.getJSONObject(i);
//                                    Tip tip = new Tip();
//
//                                    tip.setTip_id(obj.getString("tip_id"));
//                                    tip.setHome_team(obj.getString("home_team"));
//                                    tip.setAway_team(obj.getString("away_team"));
//                                    tip.setDate(obj.getString("date"));
//                                    tip.setKick_off(obj.getString("kick_off"));
//                                    tip.setOdd(obj.getString("odd"));
//                                    tip.setPrediction(obj.getString("prediction"));
//                                    tip.setScore(obj.getString("score"));
//                                    tip.setResult(obj.getString("result"));
//                                    tip.setBought(obj.getString("bought"));
//                                    tip.setImage(obj.getString("image"));
//                                    tip.setOnsale(obj.getString("onsale"));
//
//                                    tipList.add(tip);
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
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
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(tipReq);
//    }

    public void checkForUpdate() {
        try {
            UpdateChecker checker = new UpdateChecker(this);
            checker.setNotice(Notice.DIALOG);
            checker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mymenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_account) {
            if (!session.isUserLoggedIn()) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                //   builder1.setTitle("You are not loged in");
                builder1.setMessage("You have to be logged in to view your account.");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Log me in", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {
                Intent i = new Intent(MainActivity.this, AccountActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                return true;
            }
        }

        if (id == R.id.action_refresh) {
            new getTips().execute();
            loadDates();
            return true;
        }

        if (id == R.id.action_logout) {
            try {
                session.logout();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        if (id == R.id.action_about) {
            try {
                aboutUs();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void aboutUs() {

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        Element adsElement = new Element();
        //  adsElement.setTitle("Contact Us");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("BET MWITU")
                // .setImage(R.mipmap.ic_launcher)
                .addItem(new Element().setTitle(version))
                .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("betmwitu@gmail.com")
                // .addWebsite("http://www.sikumojaventures.com/")
                //  .addFacebook("www.facebook.com/betmwitu")
                .addTwitter("@betmwitu")
                .addPlayStore("com.betmwitu")
                .addItem(getCopyRightsElement())
                .create();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        dialogBuilder.setCancelable(true);
        dialogBuilder.setView(aboutPage);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        //   copyRightsElement.setIcon(R.drawable.about_icon_copy_right);
        copyRightsElement.setColor(ContextCompat.getColor(this, mehdi.sakout.aboutpage.R.color.about_item_icon_color));
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    public void refreshAnim() {
        try {
            MenuItem menuItem = mymenu.findItem(R.id.action_refresh);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
            rotation.setRepeatCount(Animation.INFINITE);
            iv.startAnimation(rotation);
            menuItem.setActionView(iv);
            DOING_REFRESH_ANIM = true;
        } catch (Exception e) {
            Log.e(TAG, "Shitty menu didnt finish loading");
        }
    }

    public void stopRefreshAnim() {
        try {
            MenuItem m = mymenu.findItem(R.id.action_refresh);
            if (m.getActionView() != null) {
                m.getActionView().clearAnimation();
                m.setActionView(null);
                DOING_REFRESH_ANIM = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Shitty menu didnt finish loading");
        }
    }

    public void onShareClick(String tip) {

        String downloadApp = "Download Bet Mwitu: https://play.google.com/store/apps/details?id=com.betmwitu";

        List<Intent> targetShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        List<ResolveInfo> resInfos = getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {

            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                Log.i("Package Name", packageName);
                if (packageName.contains("com.twitter.android") ||
                        packageName.contains("com.facebook.katana") ||
                        packageName.contains("com.facebook.lite") ||
                        packageName.contains("com.whatsapp") ||
                        packageName.contains("com.twidroid") ||
                        packageName.contains("com.handmark.tweetcaster") ||
                        packageName.contains("com.thedeck.android")) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, tip + "\n\n" + downloadApp);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Bet Mwitu");
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {
                System.out.println("Have Intent");
                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Share tip with your friends");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            } else {
                System.out.println("Do not Have Intent");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void getPhoneDetails(){
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                IMEI = telephonyManager.getDeviceId();

                MODEL = android.os.Build.MODEL;
                MANUFACTURER = android.os.Build.MANUFACTURER;
                ANDROID_VERSION = "" + Build.VERSION.RELEASE;
                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                APP_VERSION = pInfo.versionName;

                if(cd.isConnectingToInternet()){
                    new getDeviceLastSeen().execute();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    class getDeviceLastSeen extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            try {

                SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                String last_seen = s.format(new Date());

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("imei", IMEI));
                params.add(new BasicNameValuePair("manufacturer", MANUFACTURER));
                params.add(new BasicNameValuePair("model", MODEL));
                params.add(new BasicNameValuePair("android_version", ANDROID_VERSION));
                params.add(new BasicNameValuePair("app_version", APP_VERSION));
                params.add(new BasicNameValuePair("user_phone", session.getPhone()));
                params.add(new BasicNameValuePair("last_seen", last_seen));

                JSONObject json = jsonParser.makeHttpRequest(lastseen_url, "POST", params);

                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
        }
    }

    class buyTip extends AsyncTask<String, String, String> {
        int success = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Processing transaction");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            try {

                String tip_id = args[0];
                String tip_price = args[1];
                String home_away = args[2];

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tip_id", tip_id));
                params.add(new BasicNameValuePair("tip_price", tip_price));
                params.add(new BasicNameValuePair("home_away", home_away));
                params.add(new BasicNameValuePair("phone", session.getPhone()));

                JSONObject json = jsonParser.makeHttpRequest(buytip_url, "POST", params);

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
                Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

            if (success == 1) {
                new getTips().execute();
            }

        }
    }

    class getTips extends AsyncTask<String, String, String> {

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
                params.add(new BasicNameValuePair("date", dateParam));
                params.add(new BasicNameValuePair("phone", session.getPhone()));

                JSONObject json = jsonParser.makeHttpRequest(tip_url, "POST", params);

                success = json.getInt(Config.TAG_SUCCESS);
                if (success == 1) {
                    tipList.clear();

                    JSONArray tips = null;

                    try {
                        tips = json.getJSONArray("tips");
                        Log.e(TAG, tips.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (tips != null) {
                            for (int i = 0; i < tips.length(); i++) {


                                JSONObject obj = tips.getJSONObject(i);
                                Tip tip = new Tip();

                                tip.setTip_id(obj.getString("tip_id"));
                                tip.setHome_team(obj.getString("home_team"));
                                tip.setAway_team(obj.getString("away_team"));
                                tip.setDate(obj.getString("date"));
                                tip.setKick_off(obj.getString("kick_off"));
                                tip.setOdd(obj.getString("odd"));
                                tip.setPrediction(obj.getString("prediction"));
                                tip.setScore(obj.getString("score"));
                                tip.setResult(obj.getString("result"));
                                tip.setBought(obj.getString("bought"));
                                tip.setImage(obj.getString("image"));
                                tip.setOnsale(obj.getString("onsale"));
                                tip.setPrice(obj.getString("price"));

                                tipList.add(tip);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                return json.getString(Config.TAG_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            stopRefreshAnim();
            adapter.notifyDataSetChanged();
            if (file_url != null) {
                //  Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}
