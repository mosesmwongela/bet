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
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

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

        date_kick_off.setText(m.getKick_off()+", "+m.getDate());

        prediction_odd.setText(m.getPrediction()+", "+m.getOdd());

        result.setText(m.getResult());

        return convertView;
    }

}
