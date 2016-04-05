package com.project.notifuzz;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Siddharth on 06-Apr-16.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView textView,textView2,textView3;
    ImageView imageView;
    public RecyclerViewHolder(View convertView) {
        super(convertView);
        imageView = (ImageView) convertView.findViewById(R.id.imageView);
        textView = (TextView) convertView.findViewById(R.id.textView);
        textView2 = (TextView) convertView.findViewById(R.id.textView2);
        textView3 = (TextView) convertView.findViewById(R.id.textView3);

    }
}
