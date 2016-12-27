package com.betmwitu.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.betmwitu.R;

import java.util.List;

/**
 * Created by turnkey on 12/17/2016.
 */

public class AccountListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Account> accountItems;
    private String TAG = "AccountListAdapter";

    public AccountListAdapter(Activity activity, List<Account> accountItems) {
        this.activity = activity;
        this.accountItems = accountItems;
    }

    @Override
    public int getCount() {
        return accountItems.size();
    }

    @Override
    public Object getItem(int location) {
        return accountItems.get(location);
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
            convertView = inflater.inflate(R.layout.account_list_row, null);

        ImageView thumbNail = (ImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView amount = (TextView) convertView.findViewById(R.id.amount);
        TextView trans_desc = (TextView) convertView.findViewById(R.id.trans_desc);

        Drawable ic_ray_end_arrow = convertView.getResources().getDrawable(R.mipmap.ic_ray_end_arrow);
        Drawable ic_ray_start_arrow = convertView.getResources().getDrawable(R.mipmap.ic_ray_start_arrow);

        Account acc = accountItems.get(position);

        if(acc.getTrans_type().equalsIgnoreCase("0")){
            thumbNail.setImageDrawable(ic_ray_start_arrow);
        }else{
            thumbNail.setImageDrawable(ic_ray_end_arrow);
        }

        amount.setText("Ksh "+acc.getTrans_amount());
        trans_desc.setText(acc.getDesc());

        return convertView;
    }

}
