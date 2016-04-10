package com.project.notifuzz;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private ArrayList<NotificationView> notificationView = new ArrayList<>();

    public RecyclerViewAdapter(ArrayList<NotificationView> notificationView) {
        this.notificationView=notificationView;
    }

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

        holder.textView.setText(notificationView.get(position).appName+ "");
        Log.v("fgdfg", "dfgdf");
        holder.textView2.setText(notificationView.get(position).notiHead+ "");
        holder.textView3.setText(notificationView.get(position).notiContent + "");
        holder.imageView.setImageBitmap(notificationView.get(position).appIcon);
        // Log.v("fddf", notificationView.get(position) + "");
        if (notificationView.get(position).priorty == 0)
            holder.imageView2.setBackgroundResource(R.color.yellow);
        else if (notificationView.get(position).priorty > 0)
            holder.imageView2.setBackgroundResource(R.color.red);
        else
            holder.imageView2.setBackgroundResource(R.color.green);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(notificationView.get(position).time));
        holder.textView4.setText(formatter.format(calendar.getTime()));
    }

    public long getItemId(int position) {
        return 0;
    }

    //update recyclerview arraylists
    public void updateList(ArrayList<NotificationView> notificationViews) {
       this.notificationView = notificationViews;
    }

    @Override
    public int getItemCount() {
        if (this.notificationView != null)
            return this.notificationView.size();
        else
            return 0;
    }
}