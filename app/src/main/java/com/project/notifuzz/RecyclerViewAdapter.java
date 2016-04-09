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
    private ArrayList<Integer> priority;

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create viewholder for an entry
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.listview_card, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        //set data for the viewholder of any entry
        holder.textView.setText(appName.get(position) + "");
        Log.v("fgdfg", "dfgdf");
        holder.textView2.setText(notiHead.get(position) + "");
        holder.textView3.setText(notiContent.get(position) + "");
        holder.imageView.setImageBitmap(bitmaps.get(position));
        Log.v("fddf", priority.get(position) + "");
        if (priority.get(position) == 0)
            holder.imageView2.setBackgroundResource(R.color.yellow);
        else if (priority.get(position) > 0)
            holder.imageView2.setBackgroundResource(R.color.red);
        else
            holder.imageView2.setBackgroundResource(R.color.green);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time.get(position)));
        holder.textView4.setText(formatter.format(calendar.getTime()));
    }

    public long getItemId(int position) {
        return 0;
    }

    //update recyclerview arraylists
    public void updateList(ArrayList<Bitmap> bitmaps, ArrayList<String> appName, ArrayList<String> notiHead, ArrayList<String> notiContent, ArrayList<String> id, ArrayList<PendingIntent> pendingIntents, ArrayList<String> time, ArrayList<Integer> priority) {
        this.bitmaps = bitmaps;
        this.appName = appName;
        this.notiHead = notiHead;
        this.notiContent = notiContent;
        this.id = id;
        this.pendingIntents = pendingIntents;
        this.time = time;
        this.priority = priority;
    }

    @Override
    public int getItemCount() {
        if (this.appName != null)
            return this.appName.size();
        else
            return 0;
    }
}