package com.shivaconsulting.agriapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shivaconsulting.agriapp.R;

import java.util.List;

public class FeedbacksListAdapter extends BaseAdapter {
    Activity listVActivity;
    List<String> feedbackList;

    LayoutInflater inflater;
    public FeedbacksListAdapter(Activity listViewActivity,List<String> feedbackList) {
        this.listVActivity=listViewActivity;
        this. feedbackList=feedbackList;
        inflater=(LayoutInflater)listVActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return feedbackList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.text_group,null);
        TextView tv=convertView.findViewById(R.id.tvFeedbacksList);
        tv.setText(feedbackList.get(position));
        return convertView;
    }
}
