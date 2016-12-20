package com.sikumojaventures.betmoja;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.notice.Notice;
import com.sikumojaventures.betmoja.app.AppController;
import com.sikumojaventures.betmoja.auth.LoginActivity;
import com.sikumojaventures.betmoja.db.UserSessionManager;
import com.sikumojaventures.betmoja.model.CustomListAdapter;
import com.sikumojaventures.betmoja.model.Dates;
import com.sikumojaventures.betmoja.model.Tip;
import com.sikumojaventures.betmoja.util.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    ConnectionDetector cd;
    UserSessionManager session;

    private static final String url = "http://sikumojaventures.com/json/tips.json";
    private static final String date_url = "http://sikumojaventures.com/json/dates.json";
    private List<Tip> tipList = new ArrayList<Tip>();
    private List<Dates> dateList = new ArrayList<Dates>();
    private ListView listView;
    private CustomListAdapter adapter;
    private Spinner dateSpinner;

    //date to query tips
    String dateParam;

    private Boolean DOING_REFRESH_ANIM = false;

    private Menu mymenu;


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

        if(session.isUserLoggedIn()){
            checkForUpdate();

            // this thread delays for 55ms(100 ms just to be safe) so that
            // onCreateOptionsMenu happens first
            // to avoid null pointer on the menu
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadDates();
                    loadTips();
                }
            }, 100);




        }else {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    public void popoulateDateSpinner(List<Dates> dateList){
        List<String> spinnerDate =  new ArrayList<String>();

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String dateToday = df.format(Calendar.getInstance().getTime());

        for(int i=0; i<dateList.size(); i++) {
            spinnerDate.add(dateList.get(i).getDate());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerDate);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                int item = dateSpinner.getSelectedItemPosition();
                dateParam = dateSpinner.getSelectedItem().toString();
                loadTips();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        if (!dateToday.equals(null)) {
            int spinnerPosition = adapter.getPosition(dateToday);
            dateSpinner.setSelection(spinnerPosition);
        }
    }

    public void loadDates(){
        if(!DOING_REFRESH_ANIM)
        refreshAnim();

        JsonArrayRequest dateReq = new JsonArrayRequest(date_url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        stopRefreshAnim();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Dates date = new Dates();

                                date.setDate(obj.getString("date"));

                                dateList.add(date);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            popoulateDateSpinner(dateList);
                        }
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

    public void loadTips(){
        if(!DOING_REFRESH_ANIM)
        refreshAnim();

        JsonArrayRequest tipReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        stopRefreshAnim();

                        tipList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
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

                                tipList.add(tip);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                stopRefreshAnim();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(tipReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void checkForUpdate(){
        try{
            UpdateChecker checker = new UpdateChecker(this);
            checker.setNotice(Notice.DIALOG);
            checker.start();
        }catch(Exception e){
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
            return true;
        }

        if (id == R.id.action_refresh) {
            loadTips();
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
