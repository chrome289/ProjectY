package com.project.notifuzz;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    ArrayList<Bitmap> bitmaps;
    ArrayList<String> appName, notiHead, notiContent, id;
    ArrayList<PendingIntent> pendingIntents;

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.listview_card, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        holder.textView.setText(appName.get(position) + "");
        Log.v("fgdfg", "dfgdf");
        holder.textView2.setText(notiHead.get(position) + "");
        holder.textView3.setText(notiContent.get(position) + "");
        holder.imageView.setImageBitmap(bitmaps.get(position));
    }

    public long getItemId(int position) {
        return 0;
    }

    public void updateList(ArrayList<Bitmap> bitmaps, ArrayList<String> appName, ArrayList<String> notiHead, ArrayList<String> notiContent, ArrayList<String> id, ArrayList<PendingIntent> pendingIntents) {
        this.bitmaps = bitmaps;
        this.appName = appName;
        this.notiHead = notiHead;
        this.notiContent = notiContent;
        this.id = id;
        this.pendingIntents = pendingIntents;
    }

    @Override
    public int getItemCount() {
        if (this.appName != null)
            return this.appName.size();
        else
            return 0;
    }

}