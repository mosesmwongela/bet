package com.betmwitu.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.betmwitu.R;
import com.betmwitu.app.AppController;

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
        TextView tip_id = (TextView) convertView.findViewById(R.id.tip_id);
        TextView tip_price = (TextView) convertView.findViewById(R.id.tip_price);

        Tip m = tipItems.get(position);

        tip_id.setText(m.getTip_id());
        tip_id.setVisibility(View.GONE);

        tip_price.setText(m.getPrice());
        tip_price.setVisibility(View.GONE);

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
            }else{
                result.setText("Share Tip");
                result.setTextColor(convertView.getResources().getColor(R.color.primary));
            }
        }

        if(m.getOnsale().equalsIgnoreCase("1")&&m.getBought().equalsIgnoreCase("0")&&m.getScore().equalsIgnoreCase("-1")) {
            prediction_odd.setText("Tip: LOCKED [Odd:" + m.getOdd()+"]");
        }else{
            prediction_odd.setText("Tip: " + m.getPrediction() + "  [Odd:" + m.getOdd()+"]");
        }

        return convertView;
    }

}
