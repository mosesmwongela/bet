package com.sikumojaventures.betmwitu.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.sikumojaventures.betmwitu.R;
import com.sikumojaventures.betmwitu.app.AppController;

import java.util.List;
import java.util.Locale;

/**
 * Created by turnkey on 12/17/2016.
 */

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Tip> tipItems;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private String TAG = "CustomListAdapter";

    AssetManager am = null;
    Typeface typeface_bold = null;
    Typeface typeface_regular = null;

    public CustomListAdapter(Activity activity, List<Tip> tipItems) {
        this.activity = activity;
        this.tipItems = tipItems;
        am = activity.getAssets();

        typeface_bold = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "bariol_bold_webfont.ttf"));

        typeface_regular = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "bariol_regular_webfont.ttf"));
    }

    @Override
    public int getCount() {
        return tipItems.size();
    }

    @Override
    public Object getItem(int location) {
        return tipItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView home_away = (TextView) convertView.findViewById(R.id.home_away);
        home_away.setTypeface(typeface_regular);
        TextView date_kick_off = (TextView) convertView.findViewById(R.id.date_kick_off);
        date_kick_off.setTypeface(typeface_regular);
        TextView prediction_odd = (TextView) convertView.findViewById(R.id.prediction_odd);
        prediction_odd.setTypeface(typeface_regular);
        TextView result = (TextView) convertView.findViewById(R.id.result);
        result.setTypeface(typeface_bold);
        TextView tip_id = (TextView) convertView.findViewById(R.id.tip_id);
        TextView tip_price = (TextView) convertView.findViewById(R.id.tip_price);
        tip_price.setTypeface(typeface_regular);
        TextView tip_price_display = (TextView) convertView.findViewById(R.id.tip_price_display);
        tip_price_display.setTypeface(typeface_regular);
        TextView tip_talk = (TextView) convertView.findViewById(R.id.tip_talk);
        tip_talk.setTypeface(typeface_regular);
        TextView tip_country = (TextView) convertView.findViewById(R.id.tip_country);
        tip_country.setTypeface(typeface_regular);

        Tip m = tipItems.get(position);

        tip_id.setText(m.getTip_id());
        tip_id.setVisibility(View.GONE);

        tip_price.setText(m.getPrice());
        tip_price.setVisibility(View.GONE);

        tip_talk.setText(m.getTalk());
        tip_talk.setVisibility(View.GONE);

        tip_country.setText(m.getCountry_name());
        tip_country.setVisibility(View.GONE);

        if(Integer.parseInt(m.getPrice())>0) {
            tip_price_display.setText("price: ksh " + m.getPrice());
        }else{
            tip_price_display.setText("price: FREE");
        }
        tip_price_display.setTextColor(convertView.getResources().getColor(R.color.buy_tip));

        thumbNail.setImageUrl(m.getImage(), imageLoader);

        home_away.setText(m.getHome_team() +" vs "+m.getAway_team());

        date_kick_off.setText("Kick off: "+m.getKick_off());

        if(!m.getScore().equalsIgnoreCase("-1")){
            date_kick_off.setText("Kick off: " + m.getKick_off() + " [Score: " + m.getScore()+"]");
            if(m.getResult().equalsIgnoreCase("1")) {
                result.setText("Won");
                result.setTextColor(convertView.getResources().getColor(R.color.won));
                tip_price_display.setVisibility(View.GONE);
            }else if(m.getResult().equalsIgnoreCase("0")) {
                result.setText("Lost");
                result.setTextColor(convertView.getResources().getColor(R.color.lost));
                tip_price_display.setVisibility(View.GONE);
            }
        }else{
            if (m.getOnsale().equalsIgnoreCase("1") && m.getBought().equalsIgnoreCase("0")) {
                result.setText("Buy Tip");
                result.setTextColor(convertView.getResources().getColor(R.color.buy_tip));
                tip_price_display.setVisibility(View.VISIBLE);
            }else{
                tip_price_display.setVisibility(View.VISIBLE);
                if(m.getTalk()!= null && m.getTalk().length()>0) {
                    result.setText("View Analysis");
                    result.setTextColor(convertView.getResources().getColor(R.color.primary));
                }else{
                    result.setText("Share Tip");
                    result.setTextColor(convertView.getResources().getColor(R.color.primary));
                }
            }
        }

        if(m.getOnsale().equalsIgnoreCase("1")&&m.getBought().equalsIgnoreCase("0")&&m.getScore().equalsIgnoreCase("-1")) {
            prediction_odd.setText("Tip: LOCKED [Odd: " + m.getOdd()+"]");
        }else{
            prediction_odd.setText("Tip: " + m.getPrediction() + "  [Odd: " + m.getOdd()+"]");
        }

        return convertView;
    }

}
