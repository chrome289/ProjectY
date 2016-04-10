package com.project.notifuzz;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
public class RecyclerParentViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;
    public ImageButton imageButton;
    public RecyclerView recyclerView;

    //default viewholder
    public RecyclerParentViewHolder(View convertView) {
        super(convertView);
        textView = (TextView) convertView.findViewById(R.id.textView);
        recyclerView = (RecyclerView) convertView.findViewById(R.id.childView);
        imageButton=(ImageButton)convertView.findViewById(R.id.imageButton);
    }
}
