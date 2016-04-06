package com.project.notifuzz;


import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private ArrayList<Bitmap> bitmaps;
    private ArrayList<String> appName;
    private ArrayList<String> notiHead;
    private ArrayList<String> notiContent;
    private ArrayList<String> id;
    private ArrayList<String> time;
    private ArrayList<PendingIntent> pendingIntents;

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

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time.get(position)));
        holder.textView4.setText(formatter.format(calendar.getTime()));
    }

    public long getItemId(int position) {
        return 0;
    }

    public void updateList(ArrayList<Bitmap> bitmaps, ArrayList<String> appName, ArrayList<String> notiHead, ArrayList<String> notiContent, ArrayList<String> id, ArrayList<PendingIntent> pendingIntents,ArrayList<String>time) {
        this.bitmaps = bitmaps;
        this.appName = appName;
        this.notiHead = notiHead;
        this.notiContent = notiContent;
        this.id = id;
        this.pendingIntents = pendingIntents;
        this.time=time;
    }

    @Override
    public int getItemCount() {
        if (this.appName != null)
            return this.appName.size();
        else
            return 0;
    }


}