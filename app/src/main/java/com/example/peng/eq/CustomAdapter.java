package com.example.peng.eq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by cizhenwu on 10/21/16.
 */

public class CustomAdapter extends BaseAdapter{
    private String[] titles;
    private String[] holders;
    private String[] dates;
    private static LayoutInflater inflater=null;
    Context context;

    public CustomAdapter(ProfileActivity profileActivity, String[]titleList, String[] holderList, String[] dateList) {
        titles = titleList;
        holders = holderList;
        dates = dateList;
        context = profileActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Container
    {
        TextView titleView;
        TextView holderView;
        TextView dateView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Container container = new Container();
        View rowView;
        rowView = inflater.inflate(R.layout.upcomming, null);
        container.titleView=(TextView) rowView.findViewById(R.id.upcomming_name);
        container.holderView=(TextView) rowView.findViewById(R.id.upcoming_holder);
        container.dateView = (TextView) rowView.findViewById(R.id.upcoming_date);

        container.titleView.setText(titles[position]);
        container.holderView.setText(holders[position]);
        container.dateView.setText(dates[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+ titles[position], Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }


}
