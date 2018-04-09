package com.taurus.iharry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taurus on 16/10/7.
 */
public class contentAdapter extends ArrayAdapter<ChapterAndContent> {

    public contentAdapter(Context context, ArrayList<ChapterAndContent> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChapterAndContent chapterAndContent = getItem(position);
        if(convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_for_content,parent,false);
        }
        TextView chapter = (TextView)convertView.findViewById(R.id.textView6);
        TextView content = (TextView)convertView.findViewById(R.id.textView7);
        chapter.setTextSize(15);
        chapter.setText(chapterAndContent.getChapter());

//        chapter.setTextColor(R.color.colorPoptext);
//        chapter.setTextColor(convertView.getResources().getColor(R.color.textColor));
        content.setTextSize(15);
        content.setText(chapterAndContent.getContent());
//        content.setTextColor(convertView.getResources().getColor(R.color.textColor));

        return convertView;
    }
}
