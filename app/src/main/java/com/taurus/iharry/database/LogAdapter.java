package com.taurus.iharry.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.taurus.iharry.ChapterAndContent;
import com.taurus.iharry.R;

import java.util.ArrayList;

/**
 * Created by taurus on 16/10/12.
 */
public class LogAdapter extends ArrayAdapter<String> {
    public LogAdapter(Context context, ArrayList<String> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position);
        if(convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_for_log,parent,false);
        }
        TextView log = (TextView)convertView.findViewById(R.id.textView66);
        log.setText(item);
//        content.setTextSize(15);
        return convertView;
    }

}
