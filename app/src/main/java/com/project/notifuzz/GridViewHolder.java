package com.project.notifuzz;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GridViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;
    public RelativeLayout relativeLayout;

    //default viewholder
    public GridViewHolder(View convertView) {
        super(convertView);
        relativeLayout = (RelativeLayout) convertView.findViewById(R.id.layout);
        textView = (TextView) convertView.findViewById(R.id.tile);
    }
}