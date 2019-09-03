package com.joey.khatmalquran.ui.main;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joey.khatmalquran.R;
import com.joey.khatmalquran.data.db.entities.Part;

import java.util.List;

/**
 * Created by Joey on 11/26/2017.
 */

public class PartsListAdapter extends ArrayAdapter{

    Activity activity;
    List<Part> parts;
    ViewHolder vHolder = null;
    /*Map<String, Long> partAssociations;
    Map<String, Integer> partStates;*/

    public PartsListAdapter(Activity activity, List<Part> parts/*Map<String, Long> partAssociations, Map<String, Integer> partStates*/){
        super(activity, R.layout.layout_part_item, parts);
        this.activity = activity;
        this.parts = parts;
        /*this.parts = new ArrayList(partAssociations.keySet());
        this.partAssociations = partAssociations;
        this.partStates = partStates;*/
    }

    @Override
    public Object getItem(int position) {
        return parts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return parts.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.layout_part_item, null);
            vHolder = new ViewHolder();
            vHolder.partNameTextView = (TextView) rowView.findViewById(R.id.part_item_name_textview);
            vHolder.partAssociationTextView = (TextView) rowView.findViewById(R.id.part_item_association_textview);
            rowView.setTag(vHolder);
        }
        else{
            vHolder = (ViewHolder) rowView.getTag();
        }

        Part item = (Part) parts.get(position);
        vHolder.partNameTextView.setText(item.getPartName());

        if(item.getState() == Part.PART_STATE_UNTAKEN){
            vHolder.partNameTextView.setTextColor(Color.RED);
            vHolder.partAssociationTextView.setTextColor(Color.BLACK);
            vHolder.partAssociationTextView.setText("   -");
        }
        else if(item.getState() == Part.PART_STATE_COMPLETED){
            vHolder.partAssociationTextView.setTextColor(Color.GREEN);
            vHolder.partNameTextView.setTextColor(Color.GREEN);
            vHolder.partAssociationTextView.setText("" + item.getAssociatedPerson());
        }
        else if(item.getState() == Part.PART_STATE_TAKEN){
            vHolder.partAssociationTextView.setTextColor(Color.BLACK);
            vHolder.partNameTextView.setTextColor(Color.BLACK);
            vHolder.partAssociationTextView.setText("" + item.getAssociatedPerson());
        }

        return rowView;
    }

    private class ViewHolder{
        TextView partNameTextView, partAssociationTextView;
    }
}
