package com.sikumojaventures.betmoja.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.sikumojaventures.betmoja.R;
import com.sikumojaventures.betmoja.app.AppController;

import java.util.List;

/**
 * Created by turnkey on 12/17/2016.
 */

public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Tip> tipItems;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private String TAG = "CustomListAdapter";

    public CustomListAdapter(Activity activity, List<Tip> tipItems) {
        this.activity = activity;
        this.tipItems = tipItems;
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
        TextView date_kick_off = (TextView) convertView.findViewById(R.id.date_kick_off);
        TextView prediction_odd = (TextView) convertView.findViewById(R.id.prediction_odd);
        TextView result = (TextView) convertView.findViewById(R.id.result);

        Tip m = tipItems.get(position);

        thumbNail.setImageUrl(m.getImage(), imageLoader);

        home_away.setText(m.getHome_team() +" vs "+m.getAway_team());

        date_kick_off.setText("Kick off: "+m.getKick_off());

        if(!m.getScore().equalsIgnoreCase("-1")){
            date_kick_off.setText("Kick off: " + m.getKick_off() + " [Score: " + m.getScore()+"]");
            if(m.getResult().equalsIgnoreCase("1")) {
                result.setText("Won");
                result.setTextColor(convertView.getResources().getColor(R.color.won));
            }else if(m.getResult().equalsIgnoreCase("0")) {
                result.setText("Lost");
                result.setTextColor(convertView.getResources().getColor(R.color.lost));
            }
        }else{
            if (m.getOnsale().equalsIgnoreCase("1") && m.getBought().equalsIgnoreCase("0")) {
                result.setText("Buy Tip");
                result.setTextColor(convertView.getResources().getColor(R.color.buy_tip));
            }
        }

        if(m.getOnsale().equalsIgnoreCase("1")&&m.getBought().equalsIgnoreCase("0")&&m.getScore().equalsIgnoreCase("-1")) {
            prediction_odd.setText("Prediction: LOCKED [Odd:" + m.getOdd()+"]");
        }else{
            prediction_odd.setText("Prediction: " + m.getPrediction() + "  [Odd:" + m.getOdd()+"]");
        }

        return convertView;
    }

}