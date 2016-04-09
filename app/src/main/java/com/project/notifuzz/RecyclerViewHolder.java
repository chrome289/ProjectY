package com.project.notifuzz;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView textView, textView2, textView3, textView4;
    ImageView imageView, imageView2;

    //default viewholder
    public RecyclerViewHolder(View convertView) {
        super(convertView);
        imageView = (ImageView) convertView.findViewById(R.id.imageView);
        imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
        textView = (TextView) convertView.findViewById(R.id.textView);
        textView2 = (TextView) convertView.findViewById(R.id.textView2);
        textView3 = (TextView) convertView.findViewById(R.id.textView3);
        textView4 = (TextView) convertView.findViewById(R.id.textView4);
    }
}
