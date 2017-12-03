package com.joey.khatmalquran;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Joey on 11/25/2017.
 */

public class GroupsListAdapter extends ArrayAdapter {
    Activity activity ;
    List<Group> groups;
    ViewHolder vHolder = null;

    public GroupsListAdapter(Activity activity, List<Group> groups){
        super(activity, R.layout.layout_group_item, groups);
        this.activity = activity;
        this.groups = groups;
    }

    @Override
    public Object getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.layout_group_item, null);
            vHolder = new ViewHolder();
            vHolder.groupNameTextView = (TextView) rowView.findViewById(R.id.group_item_name_textview);
            vHolder.progressTextView = (TextView) rowView.findViewById(R.id.group_item_progress_textview);
            rowView.setTag(vHolder);
        }
        else{
            vHolder = (ViewHolder) rowView.getTag();
        }

        Group item = (Group) groups.get(position);
        vHolder.groupNameTextView.setText(item.getName());
        int total = item.getNumberOfPartsCompleted();
        vHolder.progressTextView.setText("" + total + " parts completed" + ", " + item.getNumberOfPartsTaken() + " parts in progress");

        return rowView;
    }

    private class ViewHolder{
        TextView groupNameTextView, progressTextView;
    }
}
