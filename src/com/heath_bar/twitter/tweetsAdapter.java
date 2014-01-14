package com.heath_bar.twitter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.heath_bar.twitter.Tweet;

import java.util.ArrayList;

public class tweetsAdapter extends ArrayAdapter<Tweet> {
    private LayoutInflater myInflater;
    private ArrayList<Tweet> arrList;
    private int layoutResourceId;

    String tag = "ErikLog";

    public tweetsAdapter(Context context, int layoutResourceId, ArrayList<Tweet> arrList) {
    	super(context, layoutResourceId, arrList);
        this.myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResourceId = layoutResourceId;
        this.arrList = arrList;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = this.myInflater.inflate(layoutResourceId, null);
        Tweet tweet = arrList.get(position);
        TextView tv_text = (TextView) convertView.findViewById(R.id.Text);
        TextView tv_created_at = (TextView) convertView.findViewById(R.id.CreatedAt);
        TextView tv_user_name = (TextView) convertView.findViewById(R.id.UserName);
        tv_text.setText(tweet.text);
        tv_created_at.setText(tweet.created_at);
        tv_user_name.setText(tweet.user_name);
        
        Button deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override // "Delete" button is pressed
            public void onClick(View view) {
                arrList.remove(position);
                notifyDataSetChanged();
            }
        });
        
        return convertView;
    }
}
